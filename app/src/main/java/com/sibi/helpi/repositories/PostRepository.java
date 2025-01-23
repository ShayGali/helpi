package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_POSTS;
import static com.sibi.helpi.utils.AppConstants.STORAGE_POSTS;

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

import java.util.ArrayList;
import java.util.List;

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

    public LiveData<List<ProductPost>> getPosts(@NonNull String category, @NonNull String subcategory, @NonNull String region, @NonNull String productStatus) {
        MutableLiveData<List<ProductPost>> mutableLiveData = new MutableLiveData<>();
        postsCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<ProductPost> productPosts = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        ProductPost productPost = document.toObject(ProductPost.class);
                        if (productPost != null) {
                            // filter by fields
                            if (!category.isEmpty() && !productPost.getCategory().equals(category)) {
                                continue;
                            }
                            if (!subcategory.isEmpty() && !productPost.getSubCategory().equals(subcategory)) {
                                continue;
                            }
                            if (!region.isEmpty() && !productPost.getRegion().equals(region)) {
                                continue;
                            }
                            if (!productStatus.isEmpty() && !productPost.getCondition().equals(productStatus)) {
                                continue;
                            }
                            productPosts.add(productPost);
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

    public LiveData<List<ProductPost>> getPosts() {
        return getPosts("", "", "", "");
    }

    public LiveData<Resource<String>> savePost(Postable post, byte[][] images) {
        Log.d("Repository", "Starting product post");

        MutableLiveData<Resource<String>> mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue(Resource.loading(null));

        String postId = postsCollection.document().getId();
        Log.d("Repository", "Generated product ID: " + postId);
        post.setId(postId);

        if (images != null && images.length > 0) {
            List<Task<Uri>> uploadTasks = imagesRepository.uploadPostImages(postId, images);
            Tasks.whenAllSuccess(uploadTasks)
                    .addOnSuccessListener(uriList -> {
                        // Save product data after images are uploaded
                        // convert List<Uri> to List<String>
                        List<String> uriStringList = new ArrayList<>();

                        for (Object uri : uriList) {
                            uriStringList.add(uri.toString());
                        }

                        post.setImageUrls(uriStringList);

                        savePostData(post, postId, mutableLiveData);
                    })
                    .addOnFailureListener(e ->
                            mutableLiveData.setValue(
                                    Resource.error("Failed to upload images: " + e.getMessage(), null)
                            )
                    );
        } else {
            savePostData(post, postId, mutableLiveData);
        }

        return mutableLiveData;
    }

    private void savePostData(Postable productPost, String productId, MutableLiveData<Resource<String>> mutableLiveData) {
        postsCollection.document(productId)
                .set(productPost)
                .addOnSuccessListener(aVoid ->
                        mutableLiveData.setValue(Resource.success(productId))
                )
                .addOnFailureListener(e ->
                        mutableLiveData.setValue(
                                Resource.error("Failed to save product: " + e.getMessage(), null)
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
}