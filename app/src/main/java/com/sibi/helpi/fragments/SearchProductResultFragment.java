package com.sibi.helpi.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ProductSliderAdapter;
import com.sibi.helpi.models.Product;
import java.util.ArrayList;
import java.util.List;

public class SearchProductResultFragment extends Fragment {

    private RecyclerView productRecyclerView;
    private ProductSliderAdapter productSliderAdapter;
    private List<Product> productList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_product_result, container, false);

        productRecyclerView = view.findViewById(R.id.productRecycleView);
        productRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize product list and adapter
        productList = new ArrayList<>();

        // Add dummy data TODO: replace with actual data
//        productList.add(new Product("Product 1", "Category 1", "Subcategory 1", "Region 1", "Status 1", R.drawable.blue_balon));
//        productList.add(new Product("Product 2", "Category 2", "Subcategory 2", "Region 2", "Status 2", R.drawable.red_balloon));
//        productList.add(new Product("Product 3", "Category 3", "Subcategory 3", "Region 3", "Status 3", R.drawable.blue_balon));
//        productList.add(new Product("Product 4", "Category 4", "Subcategory 4", "Region 4", "Status 4", R.drawable.red_balloon));
//        productList.add(new Product("Product 5", "Category 5", "Subcategory 5", "Region 5", "Status 5", R.drawable.blue_balon));

        productSliderAdapter = new ProductSliderAdapter(productList, product -> {
            // Handle navigation to product page
            Bundle bundle = new Bundle();
//            bundle.putString("productTitle", product.getTitle());
//            bundle.putString("productCategory", product.getCategory());
//            bundle.putString("productSubCategory", product.getSubCategory());
//            bundle.putString("productRegion", product.getRegion());
//            bundle.putString("productStatus", product.getStatus());
//            bundle.putInt("productImageResourceId", product.getImageResourceId());
            Navigation.findNavController(view).navigate(R.id.action_searchProductResultFragment_to_productFragment, bundle);
        });

        productRecyclerView.setAdapter(productSliderAdapter);

        return view;
    }
}

