package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ProductSliderAdapter;
import com.sibi.helpi.models.Product;
import com.sibi.helpi.repositories.ProductRepository;
import com.sibi.helpi.viewmodels.SearchProductViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchProductResultFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductSliderAdapter productSliderAdapter;
    private List<Product> productList;
    private SearchProductViewModel searchProductViewModel;

    public SearchProductResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchProductViewModel = new SearchProductViewModel();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_product_result, container, false);

        productRecyclerView = view.findViewById(R.id.productRecycleView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // get the search query from the bundle
        Bundle bundle = getArguments();
        assert bundle != null;
        String category = bundle.getString("category");
        String subcategory = bundle.getString("subcategory");
        String region = bundle.getString("region");
        String productStatus = bundle.getString("productStatus");

        // get the products from the repository
        LiveData<List<Product>> productsLiveData =  searchProductViewModel.getProducts(category, subcategory, region, productStatus);


        productSliderAdapter = new ProductSliderAdapter(product -> {
            // Handle navigation to product page
//            bundle.putString("productTitle", product.getTitle());
//            bundle.putString("productCategory", product.getCategory());
//            bundle.putString("productSubCategory", product.getSubCategory());
//            bundle.putString("productRegion", product.getRegion());
//            bundle.putString("productStatus", product.getStatus());
//            bundle.putInt("productImageResourceId", product.getImageResourceId());
            Navigation.findNavController(view).navigate(R.id.action_searchProductResultFragment_to_productFragment, bundle);
        });

        productRecyclerView.setAdapter(productSliderAdapter);

        // observe the products
        productsLiveData.observe(getViewLifecycleOwner(), products -> {
            productList = new ArrayList<>(products);
            productSliderAdapter.setProductList(productList);
        });

        return view;
    }
}

