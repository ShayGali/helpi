package com.sibi.helpi.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

        // Set up the spinners with default values
        setUpSpinner(categorySpinner, R.array.categories);
        setUpSpinner(subcategorySpinner, R.array.electronics_subcategories); //TODO: change the subcategories based on the category
        setUpSpinner(regionSpinner, R.array.region);
        setUpSpinner(productStatusSpinner, R.array.product_status);

        // navigate to the product page
        submitSearchButton.setOnClickListener(v -> {
            // add the search query to the bundle
            // check if the  spinner is the default value

            Bundle bundle = new Bundle();
            if (categorySpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.categories)[0])) {
                bundle.putString("category", "");
            } else {
                bundle.putString("category", categorySpinner.getSelectedItem().toString());
            }

            if (subcategorySpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.electronics_subcategories)[0])) {
                bundle.putString("subcategory", "");
            } else {
                bundle.putString("subcategory", subcategorySpinner.getSelectedItem().toString());
            }

            if (regionSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.region)[0])) {
                bundle.putString("region", "");
            } else {
                bundle.putString("region", regionSpinner.getSelectedItem().toString());
            }

            if (productStatusSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.product_status)[0])) {
                bundle.putString("productStatus", "");
            } else {
                bundle.putString("productStatus", productStatusSpinner.getSelectedItem().toString());
            }
            // navigate to the search result fragment and add the bundle
            Navigation.findNavController(view).navigate(R.id.action_searchProductFragment_to_searchProductResultFragment, bundle);
        });

        return view;
    }

    private void setUpSpinner(Spinner spinner, int arrayResId) {
        String[] items = getResources().getStringArray(arrayResId);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0; // Disable the default text
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY); // Set the default text color to gray
                } else {
                    textView.setTextColor(Color.BLACK); // Set the other items text color to black
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(0); // Set the default value
    }
}