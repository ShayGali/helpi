package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_PATH;
import static com.sibi.helpi.utils.AppConstants.POST_IMAGES_PATH;
import static com.sibi.helpi.utils.AppConstants.PROFILE_IMAGES_PATH;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImagesRepository {
    public static final long MAX_FILE_SIZE = (long) (0.5 * 1024 * 1024); // 0.5 MB


    // singleton
    private static ImagesRepository instance;
    private static final Object lock = new Object();

    public static ImagesRepository getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ImagesRepository();
            }
            return instance;
        }
    }

    private final FirebaseStorage storage;
    private final StorageReference storageReference;
    private final StorageReference postsRef;
    private final StorageReference profileRef;

    private ImagesRepository() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        postsRef = storageReference.child(COLLECTION_PATH).child(POST_IMAGES_PATH);
        profileRef = storageReference.child(COLLECTION_PATH).child(PROFILE_IMAGES_PATH);
    }


    // products
    private Task<Uri> uploadProductImage(String productUUID, byte[] data) {
        // Create the complete reference path first
        StorageReference productImagesRef = postsRef
                .child(productUUID)
                .child(System.currentTimeMillis() + ".jpg");

        // Start upload with the correct reference
        UploadTask uploadTask = productImagesRef.putBytes(data);

        // Return a Task that will complete with the download URL
        return uploadTask
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ImagesRepository", "Failed to upload image: " + task.getException());
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Get the download URL
                    return productImagesRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    Log.d("ImagesRepository", "Image uploaded successfully. URL: " + uri.toString());
                })
                .addOnFailureListener(exception -> {
                    Log.e("ImagesRepository", "Failed to upload image: " + exception);
                });
    }

    public List<Task<Uri>> uploadPostImages(String productId, byte[][] images) {
        // check if the images are not null
        if (images == null) {
            throw new IllegalArgumentException("Images cannot be null");
        }

        // check if all the images are not null and have a size greater than 0 and less than MAX_FILE_SIZE
        for (byte[] imageData : images) {
            if (imageData == null) {
                throw new IllegalArgumentException("Image data cannot be null");
            }
            if (imageData.length == 0) {
                throw new IllegalArgumentException("Image data cannot be empty");
            }
            if (imageData.length > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("Image data cannot be greater than 0.5 MB");
            }
        }

        List<Task<Uri>> uploadTasks = new ArrayList<>();
        for (byte[] imageData : images) {
            uploadTasks.add(uploadProductImage(productId, imageData));
        }
        return uploadTasks;
    }

    public LiveData<List<String>> getProductImages(String productId) {
        MutableLiveData<List<String>> imagesLiveData = new MutableLiveData<>();
        StorageReference productImagesRef = postsRef.child(productId);

        productImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    List<String> imageUrls = new ArrayList<>();
                    for (StorageReference item : listResult.getItems()) {
                        item.getDownloadUrl().addOnSuccessListener(uri -> {
                            imageUrls.add(uri.toString());
                            if (imageUrls.size() == listResult.getItems().size()) {
                                imagesLiveData.setValue(imageUrls);
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ProductRepository", "Failed to fetch images: " + e.getMessage());
                    imagesLiveData.setValue(null);
                });

        return imagesLiveData;
    }

    // profile
    public Task<Uri> uploadProfileImage(String userUUID, byte[] data) {
        // Create the complete reference path first
        StorageReference profileImagesRef = profileRef
                .child(userUUID)
                .child(System.currentTimeMillis() + ".jpg");

        // Start upload with the correct reference
        UploadTask uploadTask = profileImagesRef.putBytes(data);

        // Return a Task that will complete with the download URL
        return uploadTask
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ImagesRepository", "Failed to upload image: " + task.getException());
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Get the download URL
                    return profileImagesRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    Log.d("ImagesRepository", "Image uploaded successfully. URL: " + uri.toString());
                })
                .addOnFailureListener(exception -> {
                    Log.e("ImagesRepository", "Failed to upload image: " + exception);
                });
    }

    public LiveData<String> getProfileImage(String userUUID) {
        MutableLiveData<String> imageLiveData = new MutableLiveData<>();
        StorageReference profileImagesRef = profileRef.child(userUUID);

        profileImagesRef.listAll()
                .addOnSuccessListener(listResult -> {
                    if (!listResult.getItems().isEmpty()) {
                        listResult.getItems().get(0).getDownloadUrl().addOnSuccessListener(uri -> {
                            imageLiveData.setValue(uri.toString());
                        });
                    } else {
                        imageLiveData.setValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ImagesRepository", "Failed to fetch images: " + e.getMessage());
                    imageLiveData.setValue(null);
                });

        return imageLiveData;
    }

    public Task<Void> deleteProfileImage(String userUUID) {
        StorageReference profileImagesRef = profileRef.child(userUUID);
        return profileImagesRef.delete();
    }
}
