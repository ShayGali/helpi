package com.sibi.helpi.repositories;

import static com.sibi.helpi.utils.AppConstants.COLLECTION_PATH;
import static com.sibi.helpi.utils.AppConstants.POST_IMAGES_PATH;
import static com.sibi.helpi.utils.AppConstants.PROFILE_IMAGES_PATH;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
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
            uploadTasks.add(uploadPostImage(productId, imageData));
        }
        return uploadTasks;
    }

    /**
     * Fetches the images of a product from the storage
     *
     * @param productId the UUID of the product
     * @return a LiveData object that will complete with a list of image URLs
     */
    public LiveData<List<String>> getProductImages(String productId) { //TODO - change to postable
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


    /**
     * Fetches the profile image of a user from the storage
     *
     * @param userUUID the UUID of the user
     * @return a LiveData object that will complete with the image URL
     */
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

    /**
     * Deletes the profile image of a user from the storage. if the image is not found, it is treated as already deleted.
     *
     * @param userUUID
     * @return
     */
    public Task<Void> deleteProfileImage(String userUUID) {
        StorageReference profileImagesRef = profileRef.child(userUUID);
        Task<Void> deleteTask = profileImagesRef.delete();

        TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

        deleteTask.addOnSuccessListener(aVoid -> {
            // Successfully deleted
            taskCompletionSource.setResult(null);
        }).addOnFailureListener(exception -> {
            if (exception instanceof StorageException) {
                StorageException storageException = (StorageException) exception;
                if (storageException.getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                    Log.w("ImagesRepository", "Profile image not found; treating as deleted.");
                    taskCompletionSource.setResult(null);
                    return;
                }
            }
            // For other errors, pass the exception along.
            taskCompletionSource.setException(exception);
        });

        return taskCompletionSource.getTask();
    }


    /**
     * Deletes the images of a product from the storage
     *
     * @param userUUID the UUID of the user
     * @param data     the image data as a byte array
     * @return a Task that will complete with the download URL of the uploaded image
     */
    public Task<Uri> uploadProfileImage(String userUUID, byte[] data) {
        return uploadImage(profileRef, userUUID, data);
    }


    /**
     * Uploads a single image to the storage
     *
     * @param productUUID the UUID of the product
     * @param data        the image data as a byte array
     * @return a Task that will complete with the download URL of the uploaded image
     */
    private Task<Uri> uploadPostImage(String productUUID, byte[] data) {
        return uploadImage(postsRef, productUUID, data);
    }


    private Task<Uri> uploadImage(StorageReference baseRef, String folderId, byte[] data) {
        // Create a child reference with the folder ID and a timestamped file name
        StorageReference imageRef = baseRef.child(folderId)
                .child(System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = imageRef.putBytes(data);

        return uploadTask
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("ImagesRepository", "Failed to upload image: " + task.getException());
                        throw Objects.requireNonNull(task.getException());
                    }
                    // Get the download URL
                    return imageRef.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> {
                    Log.d("ImagesRepository", "Image uploaded successfully. URL: " + uri.toString());
                })
                .addOnFailureListener(exception -> {
                    Log.e("ImagesRepository", "Failed to upload image: " + exception);
                });
    }

}
