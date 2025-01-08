package com.sibi.helpi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.sibi.helpi.Resource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This class is responsible for managing the products in the Firebase Realtime Database.
 * It provides methods to post a product to the database.
 */
public class ProductRepository {
    private DatabaseReference databaseReference;

    public ProductRepository() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("products");
    }

    public LiveData<Resource<String>> postProduct(Product product) {
        MutableLiveData<Resource<String>> mutableLiveData = new MutableLiveData<>();
        String productId = databaseReference.push().getKey();

        if (productId != null) {
            product.setId(productId);
            databaseReference.child(productId).setValue(product)
                    .addOnSuccessListener(aVoid -> mutableLiveData.setValue(Resource.success(productId)))

                    .addOnFailureListener(e -> mutableLiveData.setValue(Resource.error(e.getMessage(), null)));
        } else {
            mutableLiveData.setValue(Resource.error("Failed to generate product ID", null));
        }

        return mutableLiveData;
    }
}
