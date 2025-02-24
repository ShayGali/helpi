package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_POSTS;
import static com.sibi.helpi.utils.AppConstants.IMG_UPLOAD_FAILED;
import static com.sibi.helpi.utils.AppConstants.POST_UPLOAD_FAILED;
import static com.sibi.helpi.utils.AppConstants.STORAGE_POSTS;
import static com.sibi.helpi.utils.AppConstants.PostStatus;
import static com.sibi.helpi.utils.LocationUtil.isWithinRadius;
import static com.sibi.helpi.utils.AppConstants.MAX_RADIUS;


import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Resource;
import com.sibi.helpi.models.ServicePost;
import com.sibi.helpi.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TODO: separate this file into two files: one for saving data in the database and the other for fetching data from the database!!!!
 * TODO: handle errors in the image upload process, delete the post if the image upload fails.
 * TODO: make smart pulls from the database, only pull the posts that are necessary, instead of pulling all the posts and filtering them.
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

    private final CollectionReference postsCollection;
    private final StorageReference storageReference;
    private final ImagesRepository imagesRepository;



    private PostRepository() {
        imagesRepository = ImagesRepository.getInstance();
        postsCollection = FirebaseFirestore.getInstance().collection(COLLECTION_POSTS);
        storageReference = FirebaseStorage.getInstance().getReference().child(STORAGE_POSTS);
    }

//    public LiveData<List<Postable>> getPosts(@NonNull String category, @NonNull String subcategory, @NonNull GeoPoint region, @NonNull String productStatus) {
//        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();
//        postsCollection.get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<Postable> postableList = new ArrayList<>();
//                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
//                        if (document == null) {
//                            continue;
//                        }
//
//                        Long t = document.getLong("type");
//                        if (t == null) {
//                            Log.e("Repository", "Failed to fetch products: type is null");
//                            continue;
//                        }
//
//                        AppConstants.PostType type = AppConstants.PostType.values()[Math.toIntExact(t)];
//                        Postable postable = null;
//                        try {
//                            if (type == AppConstants.PostType.PRODUCT) {
//                                postable = document.toObject(ProductPost.class);
//
//                                // Check product status only if it's a product post and status filter is not empty
//                                if (!productStatus.isEmpty() && postable != null) {
//                                    String condition = ((ProductPost) postable).getCondition();
//                                    if (condition == null || !condition.equals(productStatus)) {
//                                        continue;
//                                    }
//                                }
//                            } else if (type == AppConstants.PostType.SERVICE) {
//                                postable = document.toObject(ServicePost.class);
//                            } else {
//                                Log.w("Repository", "Unknown post type: " + type);
//                                continue;
//                            }
//
//                            if (postable != null) {
//                                // Apply filters only if they are not empty and the corresponding field exists
//                                if (!category.isEmpty() &&
//                                        (postable.getCategory() == null || !postable.getCategory().equals(category))) {
//                                    continue;
//                                }
//                                if (!subcategory.isEmpty() &&
//                                        (postable.getSubCategory() == null || !postable.getSubCategory().equals(subcategory))) {
//                                    continue;
//                                }
//                                // TODO: fix it
////                                if (!region.isEmpty() &&
////                                        (postable.getRegion() == null || !postable.getRegion().equals(region))) {
////                                    continue;
////                                }
//                            if (!productStatus.isEmpty() && !((ProductPost) postable).getCondition().equals(productStatus)) {
//                                continue;
//                            }
//                        } else if (type == AppConstants.PostType.SERVICE) {
//                            postable = document.toObject(ServicePost.class);
//                        } else {
//                            throw new IllegalArgumentException("Unknown type: " + type);
//                        }
//                        Log.d("Repository", "Postable: " + postable);
//                        if (postable != null) {
//                            // filter by fields
//                            if (!category.isEmpty() && !postable.getCategory().equals(category)) {
//                                continue;
//                            }
//                            if (!subcategory.isEmpty() && !postable.getSubCategory().equals(subcategory)) {
//                                continue;
//                            }
//                            //TODO
////                            if (!region.isEmpty() && !postable.getRegion().equals(region)) {
////                                continue;
////                            }
//
//                                // Check post status
//                                if (postable.getStatus() == null || postable.getStatus() != PostStatus.APPROVED) {
//                                    continue;
//                                }
//
//                                postableList.add(postable);
//                            }
//                        } catch (Exception e) {
//                            Log.e("Repository", "Error processing document: " + document.getId(), e);
//                            // TODO: handle this error
//                            continue;
////                            if(postable.getStatus() != null && postable.getStatus() != PostStatus.APPROVED ) {
////                                continue;
////                            }
//
////                            postableList.add(postable);
//                        }
//                    }
//                    mutableLiveData.setValue(postableList);
//                })
//                .addOnFailureListener(e -> {
//                    Log.e("Repository", "Failed to fetch products: " + e.getMessage());
//                    mutableLiveData.setValue(null);
//                });
//
//        return mutableLiveData;
//    }

    private LiveData<List<Postable>> getAllPosts(){
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
                        try {
                            if (type == AppConstants.PostType.PRODUCT) {
                                postable = document.toObject(ProductPost.class);
                            } else if (type == AppConstants.PostType.SERVICE) {
                                postable = document.toObject(ServicePost.class);
                            } else {
                                Log.w("Repository", "Unknown post type: " + type);
                                continue;
                            }

                            if (postable != null && postable.getStatus() == PostStatus.APPROVED) {
                                postableList.add(postable);
                            }
                        } catch (Exception e) {
                            Log.e("Repository", "Error processing document: " + document.getId(), e);
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

    public LiveData<List<Postable>> getPosts(@NonNull String category, @NonNull String subcategory, @NonNull GeoPoint location, @NonNull String productStatus, @NonNull AppConstants.PostType postType) {
        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();

        if (postType == AppConstants.PostType.ANY) {
            return getAllPosts();
        }

        // 🔹 Step 1: Build Firestore Query Dynamically
        Query query = postsCollection;

        query = query.whereEqualTo("type", postType.ordinal())
                .whereEqualTo("status", PostStatus.APPROVED);

        if (!category.isEmpty() && !category.equals("Find everything")) {
            query = query.whereEqualTo("category", category);
        }
        if (!subcategory.isEmpty() && !subcategory.equals("Find everything")) {
            query = query.whereEqualTo("subCategory", subcategory);
        }
        if (postType == AppConstants.PostType.PRODUCT && !productStatus.isEmpty() && !productStatus.equals("Find everything")) {
            query = query.whereEqualTo("condition", productStatus);
        }

        // 🔹 Step 2: Fetch Data from Firestore
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Postable> postableList = new ArrayList<>();
                    List<Postable> filteredByLocation = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        if (document == null) continue;

                        Postable postable;
                        try {
                            if (postType == AppConstants.PostType.PRODUCT) {
                                postable = document.toObject(ProductPost.class);
                            } else if (postType == AppConstants.PostType.SERVICE) {
                                postable = document.toObject(ServicePost.class);
                            } else {
                                Log.w("Repository", "Unknown post type: " + postType);
                                continue;
                            }

                            if (postable != null) {
                                postableList.add(postable);
                            }
                        } catch (Exception e) {
                            Log.e("Repository", "Error processing document: " + document.getId(), e);
                        }
                    }

                    // 🔹 Step 3: Apply Location Filtering
                    postableList.stream()
                            .filter(postable -> {
                                if (postable.getLocation() == null) {
                                    return true;
                                }
                                return isWithinRadius(location, postable.getLocation(), MAX_RADIUS) || location.compareTo(new GeoPoint(0, 0)) == 0;
                            })
                            .forEach(filteredByLocation::add);

                    mutableLiveData.setValue(filteredByLocation);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to fetch products: " + e.getMessage());
                    mutableLiveData.setValue(null);
                });

        return mutableLiveData;
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




    public LiveData<List<Postable>> getUnderReviewPosts() {
        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();

        Query query = postsCollection.whereEqualTo("status", PostStatus.UNDER_REVIEW);
        query.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Postable> postableList = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        if (document == null) continue;
                        Postable postable = null;
                        Long t = document.getLong("type");
                        if (t == null) {
                            Log.e("Repository", "Failed to fetch products: type is null");
                            continue;
                        }
                        if (t == AppConstants.PostType.PRODUCT.ordinal()) {
                            postable = document.toObject(ProductPost.class);
                        } else if (t == AppConstants.PostType.SERVICE.ordinal()) {
                            postable = document.toObject(ServicePost.class);
                        }
                        if (postable != null && postable.getStatus() == PostStatus.UNDER_REVIEW) {
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



    public LiveData<List<Postable>> getPosts(List<String> postsIds) {
        MutableLiveData<List<Postable>> mutableLiveData = new MutableLiveData<>();

        if (postsIds.isEmpty()) {
            mutableLiveData.setValue(new ArrayList<>());
            return mutableLiveData;
        }

        // Split IDs into chunks of 10
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < postsIds.size(); i += 10) {
            batches.add(postsIds.subList(i, Math.min(i + 10, postsIds.size())));
        }

        List<Postable> allPosts = new ArrayList<>();
        AtomicInteger completedQueries = new AtomicInteger(0);

        for (List<String> batch : batches) {
            postsCollection
                    .whereIn(FieldPath.documentId(), batch)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            if (document == null) continue;

                            Long t = document.getLong("type");
                            if (t == null) {
                                Log.e("Repository", "Failed to fetch products: type is null");
                                continue;
                            }

                            AppConstants.PostType type = AppConstants.PostType.values()[Math.toIntExact(t)];
                            Postable postable = null;
                            if (type == AppConstants.PostType.PRODUCT) {
                                postable = document.toObject(ProductPost.class);
                            } else if (type == AppConstants.PostType.SERVICE) {
                                postable = document.toObject(ServicePost.class);
                            } else {
                                throw new IllegalArgumentException("Unknown type: " + type);
                            }

                            if (postable != null) {
                                allPosts.add(postable);
                            }
                        }

                        // If all queries are complete, update LiveData
                        if (completedQueries.incrementAndGet() == batches.size()) {
                            mutableLiveData.setValue(allPosts);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Repository", "Failed to fetch products: " + e.getMessage());
                        mutableLiveData.setValue(null);
                    });
        }

        return mutableLiveData;
    }

    public Task<List<Postable>> getRecentPosts(int numberOfPost, AppConstants.PostType postType) {
        Query query = postsCollection;
        query = query.whereEqualTo("type", postType.ordinal());
        query = query.whereEqualTo("status", PostStatus.APPROVED);
        query = query.orderBy("timestamp", Query.Direction.DESCENDING);
        query = query.limit(numberOfPost);



        return query.get().continueWith(task -> {
            if (task.isSuccessful()) {
                List<Postable> postableList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Postable postable = null;
                    if (postType == AppConstants.PostType.PRODUCT) {
                        postable = document.toObject(ProductPost.class);
                    } else if (postType == AppConstants.PostType.SERVICE) {
                        postable = document.toObject(ServicePost.class);
                    }
                    if (postable != null) {
                        postableList.add(postable);
                    }
                }
                Log.d("Repository", "Fetched " + postableList.size() + " posts");
                return postableList;
            } else {
                Log.e("Repository", "Failed to fetch recent posts: " + task.getException());
                return null;
            }
        });
    }



}