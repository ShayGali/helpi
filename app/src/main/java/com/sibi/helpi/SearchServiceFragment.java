package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchServiceFragment extends Fragment {

    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private Spinner regionSpinner;

    public SearchServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_service, container, false);
        Button submitSearchButton = view.findViewById(R.id.btnSearchProduct);

        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);

        // navigate to the product page
        submitSearchButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_searchProductFragment_to_searchProductResultFragment);
        });

        String[] categories = getResources().getStringArray(R.array.categories);
        String[] electronicsSubcategories = getResources().getStringArray(R.array.electronics_subcategories);
        String[] regions = getResources().getStringArray(R.array.region);

        categorySpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, categories));
        subcategorySpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, electronicsSubcategories));
        regionSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, regions));
        return view;
    }
}