package com.sibi.helpi.viewmodels;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.Product;
import com.sibi.helpi.ProductRepository;
import com.sibi.helpi.Resource;

import java.util.List;

public class OfferProductViewModel extends ViewModel {
    private ProductRepository productRepository;  // Repository to handle the post product operation
    private MutableLiveData<Resource<String>> postProductLiveData = new MutableLiveData<>();

    public OfferProductViewModel() {
        productRepository = new ProductRepository();
    }

    public LiveData<Resource<String>> getPostProductLiveData() {
        return postProductLiveData;
    }

    public void postProduct(Product product, List<Uri> imageUris) {
        Log.d("ViewModel", "Posting product: " + product.toString());
        postProductLiveData.setValue(Resource.loading(null));
        productRepository.postProduct(product, imageUris)
                .observeForever(result -> {
                    Log.d("ViewModel", "Got result: " + result.getStatus());
                    postProductLiveData.setValue(result);
                });
    }
}
