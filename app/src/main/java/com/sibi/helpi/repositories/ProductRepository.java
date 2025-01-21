package com.sibi.helpi.repositories;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sibi.helpi.models.Product;
import com.sibi.helpi.models.Resource;

import java.util.ArrayList;
import java.util.List;

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

    private CollectionReference productCollection;
    private StorageReference storageReference;
    private ImagesRepository imagesRepository;

    private static final String COLLECTION_PRODUCTS = "products";
    private static final String STORAGE_PRODUCTS = "product_images";

    private ProductRepository() {
        imagesRepository = ImagesRepository.getInstance();

        // Initialize Firestore collection reference
        productCollection = FirebaseFirestore.getInstance().collection(COLLECTION_PRODUCTS);

        // Initialize Storage reference
        storageReference = FirebaseStorage.getInstance().getReference().child(STORAGE_PRODUCTS);
    }

    public LiveData<List<Product>> getProducts(@NonNull String category, @NonNull String subcategory, @NonNull String region, @NonNull String productStatus) {
        MutableLiveData<List<Product>> mutableLiveData = new MutableLiveData<>();
        productCollection.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Product> products = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        Product product = document.toObject(Product.class);
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

    public LiveData<List<Product>> getProducts() {
        return getProducts("", "", "", "");
    }

    public LiveData<Resource<String>> postProduct(Product product, byte[][] images) {
        MutableLiveData<Resource<String>> mutableLiveData = new MutableLiveData<>();
        Log.d("Repository", "Starting product post");

        mutableLiveData.setValue(Resource.loading(null));
        String productId = productCollection.document().getId();

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

                        for (Object uri : uriList) {
                            uriStringList.add(uri.toString());
                        }

                        product.setImageUrls(uriStringList);

                        saveProductData(product, productId, mutableLiveData);
                    })
                    .addOnFailureListener(e ->
                            mutableLiveData.setValue(
                                    Resource.error("Failed to upload images: " + e.getMessage(), null)
                            )
                    );
        } else {
            saveProductData(product, productId, mutableLiveData);
        }

        return mutableLiveData;
    }

    private void saveProductData(Product product, String productId, MutableLiveData<Resource<String>> mutableLiveData) {
        productCollection.document(productId)
                .set(product)
                .addOnSuccessListener(aVoid ->
                        mutableLiveData.setValue(Resource.success(productId))
                )
                .addOnFailureListener(e ->
                        mutableLiveData.setValue(
                                Resource.error("Failed to save product: " + e.getMessage(), null)
                        )
                );
    }

    public LiveData<Resource<Void>> deleteProduct(String productId) {
        MutableLiveData<Resource<Void>> mutableLiveData = new MutableLiveData<>();

        // Set initial loading state
        mutableLiveData.setValue(Resource.loading(null));

        // Delete product data
        productCollection.document(productId)
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