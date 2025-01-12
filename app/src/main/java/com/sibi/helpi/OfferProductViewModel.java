package com.sibi.helpi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.Resource;

public class OfferProductViewModel extends ViewModel {
    private ProductRepository productRepository;
    private MutableLiveData<Resource<String>> postProductLiveData = new MutableLiveData<>();

    public OfferProductViewModel() {
        productRepository = new ProductRepository();
    }

    public LiveData<Resource<String>> getPostProductLiveData() {
        return postProductLiveData;
    }

    public void postProduct(Product product) {
        postProductLiveData.setValue(Resource.loading(null));
        productRepository.postProduct(product).observeForever(postProductLiveData::setValue);
    }
}
