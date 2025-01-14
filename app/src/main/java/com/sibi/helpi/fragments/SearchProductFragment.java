package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sibi.helpi.R;

public class SearchProductFragment extends Fragment {

    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private Spinner regionSpinner;
    private Spinner productStatusSpinner;

    public SearchProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_product, container, false);
        Button submitSearchButton = view.findViewById(R.id.btnSearchProduct);

        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        productStatusSpinner = view.findViewById(R.id.spinnerProductCondition);

        // navigate to the product page
        //TODO: if a filter is not selected, replace it with empty string ("")
        submitSearchButton.setOnClickListener(v -> {
            // add the search query to the bundle
            Bundle bundle = new Bundle();
            bundle.putString("category", categorySpinner.getSelectedItem().toString());
            bundle.putString("subcategory", subcategorySpinner.getSelectedItem().toString());
            bundle.putString("region", regionSpinner.getSelectedItem().toString());
            bundle.putString("productStatus", productStatusSpinner.getSelectedItem().toString());

            Navigation.findNavController(view).navigate(R.id.action_searchProductFragment_to_searchProductResultFragment);
        });

        String[] categories = getResources().getStringArray(R.array.categories);
        String[] electronicsSubcategories = getResources().getStringArray(R.array.electronics_subcategories);
        String[] regions = getResources().getStringArray(R.array.region);
        String[] productStatus = getResources().getStringArray(R.array.product_status);

        categorySpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories));
        subcategorySpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, electronicsSubcategories));
        regionSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, regions));
        productStatusSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, productStatus));
        return view;
    }
}