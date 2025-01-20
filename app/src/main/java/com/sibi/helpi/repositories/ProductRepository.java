package com.sibi.helpi.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sibi.helpi.models.Product;
import com.sibi.helpi.models.Resource;

/**
 * This class is responsible for managing the products in the Firebase Realtime Database.
 * It provides methods to post a product to the database.
 */
public class ProductRepository {
    public static ProductRepository instance;

    private static final Object LOCK = new Object();

    public synchronized static ProductRepository getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ProductRepository();
                }
            }
        }
        return instance;
    }


    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ImagesRepository imagesRepository;
    private static final String NODE_PRODUCTS = "products";
    private static final String STORAGE_PRODUCTS = "product_images";

    private ProductRepository() {

        imagesRepository = ImagesRepository.getInstance();

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
     * fetches products from the database based on the search query.
     * if some a field is empty - don't filter by this field
     *
     * @param category      category of the product
     * @param subcategory   subcategory of the product
     * @param region        region of the product
     * @param productStatus status of the product
     * @return LiveData containing list of products
     */
    public LiveData<List<Product>> getProducts(@NonNull String category, @NonNull String subcategory, @NonNull String region, @NonNull String productStatus) {
        MutableLiveData<List<Product>> mutableLiveData = new MutableLiveData<>();
        databaseReference.get()
                .addOnSuccessListener(dataSnapshot -> {
                    List<Product> products = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null) {
                            // filter by fields
                            if (!category.isEmpty() && !product.getCategory().equals(category)) {
                                continue;
                            }
                            if (!subcategory.isEmpty() && !product.getSubCategory().equals(subcategory)) {
                                continue;
                            }
                            if (!region.isEmpty() && !product.getRegion().equals(region)) {
                                continue;
                            }
                            if (!productStatus.isEmpty() && !product.getCondition().equals(productStatus)) {
                                continue;
                            }
                            products.add(product);
                        }
                    }
                    mutableLiveData.setValue(products);
                })
                .addOnFailureListener(e -> {
                    Log.e("Repository", "Failed to fetch products: " + e.getMessage());
                    mutableLiveData.setValue(null);
                });

        return mutableLiveData;
    }

    /**
     * fetches all products from the database
     *
     * @return LiveData containing list of products
     */
    public LiveData<List<Product>> getProducts() {
        return getProducts("", "", "", "");
    }


    /**
     * Posts a new product with images to Firebase
     *
     * @param product   Product object to save
     * @param images array of byte arrays containing images
     * @return LiveData containing Resource with product ID
     */
    public LiveData<Resource<String>> postProduct(Product product, byte[][] images) {
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
        if (images != null && images.length > 0) {
            List<Task<Uri>> uploadTasks = imagesRepository.uploadProductImages(productId, images);
            Tasks.whenAllSuccess(uploadTasks)
                    .addOnSuccessListener(uriList -> {
                        // Save product data after images are uploaded
                        // convert List<Uri> to List<String>
                        List<String> uriStringList = new ArrayList<>();
                        for (int i = 0; i < uriList.size(); i++) {
                            uriStringList.add(uriList.get(i).toString());
                        }

                        product.setImageUrls(uriStringList);

                        saveProductData(product, productId, mutableLiveData);
                    })
                    .addOnFailureListener(e ->
                            mutableLiveData.setValue(
                                    Resource.error("Failed to upload images: " + e.getMessage(), null)
                            )
                    );
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
     *
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
