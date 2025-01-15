package com.sibi.helpi.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.sibi.helpi.models.Product;
import com.sibi.helpi.repositories.ProductRepository;

import java.util.List;

public class SearchProductViewModel extends ViewModel {
    ProductRepository productRepository;

    public SearchProductViewModel() {
        productRepository = ProductRepository.getInstance();
    }

    public LiveData<List<Product>> getProducts(String category, String subcategory, String region, String productStatus) {
        return productRepository.getProducts(category, subcategory, region, productStatus);
    }
}
