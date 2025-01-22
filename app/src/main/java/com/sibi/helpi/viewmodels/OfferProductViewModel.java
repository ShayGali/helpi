package com.sibi.helpi.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Product;
import com.sibi.helpi.repositories.ProductRepository;
import com.sibi.helpi.models.Resource;

public class OfferProductViewModel extends ViewModel {
    private ProductRepository productRepository;  // Repository to handle the post product operation
    private MutableLiveData<Resource<String>> postProductLiveData = new MutableLiveData<>();

    public OfferProductViewModel() {
        productRepository = ProductRepository.getInstance();
    }

    public LiveData<Resource<String>> getPostProductLiveData() {
        return postProductLiveData;
    }

    public void postProduct(Product product, byte[][] images) {
        postProductLiveData.setValue(Resource.loading(null));
        LiveData<Resource<String>> result = productRepository.postProduct(product, images);
        result.observeForever(new Observer<>() {
            @Override
            public void onChanged(Resource<String> stringResource) {
                postProductLiveData.setValue(stringResource);
                result.removeObserver(this);
            }
        });
    }
}
