package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_POSTS;
import static com.sibi.helpi.utils.AppConstants.IMG_UPLOAD_FAILED;
import static com.sibi.helpi.utils.AppConstants.POST_UPLOAD_FAILED;
import static com.sibi.helpi.utils.AppConstants.STORAGE_POSTS;
import static com.sibi.helpi.utils.AppConstants.PostStatus;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Resource;
import com.sibi.helpi.models.ServicePost;
import com.sibi.helpi.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: separate this file into two files: one for saving data in the database and the other for fetching data from the database!!!!
 * TODO: handle errors in the image upload process, delete the post if the image upload fails.
 */
public class PostRepository {
    public static PostRepository instance;

    private static final Object LOCK = new Object();

    public synchronized static PostRepository getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new PostRepository();
                }
            }
        }
        return instance;
    }

    private CollectionReference postsCollection;
    private StorageReference storageReference;
    private ImagesRepository imagesRepository;

    private PostRepository() {
        imagesRepository = ImagesRepository.getInstance();
        postsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_POSTS);
        storageReference = FirebaseStorage.getInstance().getReference().child(STORAGE_POSTS);
    }

    public LiveData<List<Postable>> getPosts(@NonNull String category, @NonNull String subcategory, @NonNull String region, @NonNull String productStatus, AppConstants.PostType postType) {
        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();
        postsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Postable> postableList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                        if (document == null) {
                            continue;
                        }

                        Long t = document.getLong("type");
                        if (t == null) {
                            Log.e("Repository", "Failed to fetch products: type is null");
                            continue;
                        }

                        AppConstants.PostType type = AppConstants.PostType.values()[Math.toIntExact(t)];
                        Postable postable = null;
                        if (type == AppConstants.PostType.PRODUCT) {
                            postable = document.toObject(ProductPost.class);

                            if (!productStatus.isEmpty() && !((ProductPost) postable).getCondition().equals(productStatus)) {
                                continue;
                            }
                        } else if (type == AppConstants.PostType.SERVICE) {
                            postable = document.toObject(ServicePost.class);
                        } else {
                            throw new IllegalArgumentException("Unknown type: " + type);
                        }
                        if (postable != null) {
                            // filter by fields
                            if (!category.isEmpty() && !postable.getCategory().equals(category)) {
                                continue;
                            }
                            if (!subcategory.isEmpty() && !postable.getSubCategory().equals(subcategory)) {
                                continue;
                            }
                            if (!region.isEmpty() && !postable.getRegion().equals(region)) {
                                continue;
                            }

                            if(postable.getStatus() != null && postable.getStatus() != PostStatus.APPROVED ) {
                                continue;
                            } // works well. TODO: uncomment this line after admin approval is implemented
                            postableList.add(postable);
                        }
                    }
                    mutableLiveData.setValue(postableList);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to fetch products: " + e.getMessage());
                    mutableLiveData.setValue(null);
                });

        return mutableLiveData;
    }

    public LiveData<List<Postable>> getPosts() {
        return getPosts("", "", "", "", AppConstants.PostType.PRODUCT);
    }

    public void savePost(Postable post, byte[][] images, AppConstants.PostType postType, MutableLiveData<Resource<String>> postLiveData) {
        Log.d("Repository", "Starting product post");

        String postId = postsCollection.document().getId();
        Log.d("Repository", "Generated product ID: " + postId);
        post.setId(postId);

        if (images != null && images.length > 0) {
            saveImgData(post, postType, images, postLiveData);
        } else {
            savePostData(post, postType, postLiveData);
        }
    }

    private void saveImgData(Postable post, AppConstants.PostType postType, byte[][] images, MutableLiveData<Resource<String>> postLiveData) {
        List<Task<Uri>> uploadTasks = imagesRepository.uploadPostImages(post.getId(), images);
        Tasks.whenAllSuccess(uploadTasks)
                .addOnSuccessListener(uriList -> {
                    List<String> uriStringList = new ArrayList<>();
                    for (Object uri : uriList) {
                        uriStringList.add(uri.toString());
                    }
                    post.setImageUrls(uriStringList);
                    savePostData(post, postType, postLiveData);
                })
                .addOnFailureListener(e ->
                        postLiveData.setValue(
                                Resource.error(IMG_UPLOAD_FAILED + e.getMessage(), null)
                        )
                );
    }

    private void savePostData(Postable post, AppConstants.PostType postType, MutableLiveData<Resource<String>> postLiveData) {
        postsCollection.document(post.getId())
                .set(post)
                .addOnSuccessListener(aVoid -> {
                    // Add the postType field separately
                    postsCollection.document(post.getId())
                            .update("type", postType.ordinal())
                            .addOnSuccessListener(aVoid2 ->
                                    postLiveData.setValue(Resource.success(post.getId()))
                            )
                            .addOnFailureListener(e ->
                                    postLiveData.setValue(
                                            Resource.error(POST_UPLOAD_FAILED + e.getMessage(), null)
                                    )
                            );
                })
                .addOnFailureListener(e ->
                        postLiveData.setValue(
                                Resource.error(POST_UPLOAD_FAILED + e.getMessage(), null)
                        )
                );
    }

    public LiveData<Resource<Void>> deletePost(String productId) {
        MutableLiveData<Resource<Void>> mutableLiveData = new MutableLiveData<>();

        // Set initial loading state
        mutableLiveData.setValue(Resource.loading(null));

        // Delete product data
        postsCollection.document(productId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    // Also delete associated images
                    StorageReference productImages = storageReference.child(productId);
                    productImages.listAll()
                            .addOnSuccessListener(listResult -> {
                                for (StorageReference item : listResult.getItems()) {
                                    item.delete();
                                }
                                mutableLiveData.setValue(Resource.success(null));
                            })
                            .addOnFailureListener(e ->
                                    mutableLiveData.setValue(
                                            Resource.error("Failed to delete images: " + e.getMessage(), null)
                                    )
                            );
                })
                .addOnFailureListener(e ->
                        mutableLiveData.setValue(
                                Resource.error("Failed to delete product: " + e.getMessage(), null)
                        )
                );

        return mutableLiveData;
    }

//    public LiveData<List<Postable>> getUnderReviewPosts() {
//        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();
//        postsCollection.get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<Postable> posts = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        Postable post = document.toObject(ProductPost.class);
//                        if (post != null) {
//                            if (post.getStatus() != null && post.getStatus() == PostStatus.UNDER_REVIEW) {
//                                posts.add(post);
//                            }
//                        }
//                    }
//                    mutableLiveData.setValue(posts);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Repository", "Failed to fetch posts: " + e.getMessage());
//                    mutableLiveData.setValue(null);
//                });
//
//
//        return mutableLiveData;
//    }


    public LiveData<List<ProductPost>> getUnderReviewPosts() {
        MutableLiveData<List<ProductPost>> mutableLiveData = new MutableLiveData<>();

        postsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductPost> productPosts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ProductPost productPost = document.toObject(ProductPost.class);
                        if (productPost != null) {
                            if (productPost.getStatus() != null && productPost.getStatus() == PostStatus.UNDER_REVIEW) {
                                productPosts.add(productPost);
                            }
                        }
                    }
                    mutableLiveData.setValue(productPosts);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to fetch products: " + e.getMessage());
                    mutableLiveData.setValue(null);
                });
        return mutableLiveData;
    }

    public LiveData<Boolean> updatePostStatus(String postId, PostStatus newStatus) {
        MutableLiveData<Boolean> mutableLiveData = new MutableLiveData<>();
        postsCollection.document(postId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d("PostRepository", "Post status updated successfully for postId: " + postId);
                    mutableLiveData.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("PostRepository", "Failed to update post status for postId: " + postId, e);
                    mutableLiveData.setValue(false);
                });
        return mutableLiveData;
    }
}