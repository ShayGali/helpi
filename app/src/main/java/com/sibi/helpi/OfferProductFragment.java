package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class OfferProductFragment extends Fragment {

    Spinner categorySpinner;
    Spinner subcategorySpinner;
    Spinner regionSpinner;
    Spinner productStatusSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offer_product, container, false);

        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        productStatusSpinner = view.findViewById(R.id.spinnerProductSituation);

        String[] categories = getResources().getStringArray(R.array.categories);
        //TODO: get the subcategories based on the selected category
        String[] electronicsSubcategories = getResources().getStringArray(R.array.electronics_subcategories);
        String[] regions = getResources().getStringArray(R.array.region);
        String[] productStatus = getResources().getStringArray(R.array.product_status);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(categoryAdapter);

        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, electronicsSubcategories);
        subcategorySpinner.setAdapter(subAdapter);

        ArrayAdapter<String> regionAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, regions);
        regionSpinner.setAdapter(regionAdapter);

        ArrayAdapter<String> productStatusAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, productStatus);
        productStatusSpinner.setAdapter(productStatusAdapter);


        return view;
    }
}