package com.sibi.helpi;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
        postProductLiveData.setValue(Resource.loading(null));  // Set loading state
        productRepository.postProduct(product, imageUris).observeForever(postProductLiveData::setValue);
    }
}
