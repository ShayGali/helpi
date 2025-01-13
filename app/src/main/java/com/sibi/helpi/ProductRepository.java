package com.sibi.helpi;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is responsible for managing the products in the Firebase Realtime Database.
 * It provides methods to post a product to the database.
 */
public class ProductRepository {
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final String NODE_PRODUCTS = "products";
    private static final String STORAGE_PRODUCTS = "product_images";

    public ProductRepository() {
        // Initialize Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance()
                .getReference()
                .child(NODE_PRODUCTS);

        // Initialize Storage reference
        storageReference = FirebaseStorage.getInstance()
                .getReference()
                .child(STORAGE_PRODUCTS);
    }

    /**
     * Uploads multiple images to Firebase Storage
     * @param imageUris List of image URIs to upload
     * @param productId Product ID to associate images with
     * @return Task containing list of download URLs
     */
    private Task<List<String>> uploadImages(List<Uri> imageUris, String productId) {
        List<Task<String>> uploadTasks = new ArrayList<>();

        for (Uri imageUri : imageUris) {
            // Generate unique name for each image
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageReference
                    .child(productId)
                    .child(imageName);

            // Upload file and get download URL
            UploadTask uploadTask = imageRef.putFile(imageUri);
            Task<String> urlTask = uploadTask
                    .continueWithTask(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return imageRef.getDownloadUrl();
                    })
                    .continueWith(task -> {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return task.getResult().toString();
                    });

            uploadTasks.add(urlTask);
        }

        // Combine all upload tasks
        return Tasks.whenAllSuccess(uploadTasks);
    }

    /**
     * Posts a new product with images to Firebase
     * @param product Product object to save
     * @param imageUris List of image URIs to upload
     * @return LiveData containing Resource with product ID
     */
    public LiveData<Resource<String>> postProduct(Product product, List<Uri> imageUris) {
        MutableLiveData<Resource<String>> mutableLiveData = new MutableLiveData<>();
        Log.d("Repository", "Starting product post");

        mutableLiveData.setValue(Resource.loading(null));
        String productId = databaseReference.push().getKey();

        if (productId == null) {
            Log.e("Repository", "Failed to generate product ID");
            mutableLiveData.setValue(Resource.error("Failed to generate product ID", null));
            return mutableLiveData;
        }

        Log.d("Repository", "Generated product ID: " + productId);

        // Set product ID
        product.setId(productId);

        // First upload images if any
        if (imageUris != null && !imageUris.isEmpty()) {
            uploadImages(imageUris, productId)
                    .addOnSuccessListener(imageUrls -> {
                        // Set image URLs to product
                        product.setImageUrls(imageUrls);

                        // Save product data
                        saveProductData(product, productId, mutableLiveData);
                    })
                    .addOnFailureListener(e ->
                            mutableLiveData.setValue(
                                    Resource.error("Failed to upload images: " + e.getMessage(), null)
                            )
                    );
        } else {
            // Save product without images
            saveProductData(product, productId, mutableLiveData);
        }

        return mutableLiveData;
    }

    /**
     * Helper method to save product data to Realtime Database
     */
    private void saveProductData(Product product, String productId,
                                 MutableLiveData<Resource<String>> mutableLiveData) {
        databaseReference
                .child(productId)
                .setValue(product)
                .addOnSuccessListener(aVoid ->
                        mutableLiveData.setValue(Resource.success(productId))
                )
                .addOnFailureListener(e ->
                        mutableLiveData.setValue(
                                Resource.error("Failed to save product: " + e.getMessage(), null)
                        )
                );
    }

    /**
     * Deletes a product and its images
     * @param productId ID of product to delete
     * @return LiveData containing Resource with deletion status
     */
    public LiveData<Resource<Void>> deleteProduct(String productId) {
        MutableLiveData<Resource<Void>> mutableLiveData = new MutableLiveData<>();

        // Set initial loading state
        mutableLiveData.setValue(Resource.loading(null));

        // Delete product data
        databaseReference
                .child(productId)
                .removeValue()
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
