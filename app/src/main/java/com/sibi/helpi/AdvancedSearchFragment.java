package com.sibi.helpi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

public class AdvancedSearchFragment extends Fragment {
    public AdvancedSearchFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advanced_search, container, false);

        Spinner categorySpinner = view.findViewById(R.id.spinnerCategories);
        String[] categories = {"Category 1", "Category 2", "Category 3", "Category 4"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categories);
        categorySpinner.setAdapter(adapter);

        Spinner subCategorySpinner = view.findViewById(R.id.spinnerSubCategories);
        String[] subCategories = {"SubCategory 1", "SubCategory 2", "SubCategory 3", "SubCategory 4"};
        ArrayAdapter<String> subAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, subCategories);
        subCategorySpinner.setAdapter(subAdapter);


//        categorySpinner.setOnClickListener(v -> categorySpinner.performClick());
        return view;

    }
}