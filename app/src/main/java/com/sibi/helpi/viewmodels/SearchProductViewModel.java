package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Product;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.repositories.ProductRepository;

import java.util.List;

public class SearchProductViewModel extends ViewModel {
    private final    ProductRepository productRepository;
    private final ImagesRepository imagesRepository;

    public SearchProductViewModel() {
        productRepository = ProductRepository.getInstance();
        imagesRepository = ImagesRepository.getInstance();
    }

    public LiveData<List<Product>> getProducts(String category, String subcategory, String region, String productStatus) {
        return productRepository.getProducts(category, subcategory, region, productStatus);
    }

    public LiveData<List<String>> getProductImages(String productId) {
        return imagesRepository.getProductImages(productId);
    }
}
