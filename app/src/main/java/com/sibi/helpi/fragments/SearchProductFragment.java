package com.sibi.helpi.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.sibi.helpi.R;

public class SearchProductFragment extends Fragment {

    private AutoCompleteTextView categorySpinner, subcategorySpinner, regionSpinner, productStatusSpinner;
    private TextInputLayout categoryInputLayout, subcategoryInputLayout, regionInputLayout, productStatusInputLayout;

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

        categorySpinner = view.findViewById(R.id.categorySpinner);
        subcategorySpinner = view.findViewById(R.id.subcategorySpinner);
        regionSpinner = view.findViewById(R.id.regionSpinner);
        productStatusSpinner = view.findViewById(R.id.productStatusSpinner);

        // Reference to the TextInputLayouts
        categoryInputLayout = view.findViewById(R.id.categoryInputLayout);
        subcategoryInputLayout = view.findViewById(R.id.subcategoryInputLayout);
        regionInputLayout = view.findViewById(R.id.regionInputLayout);
        productStatusInputLayout = view.findViewById(R.id.productStatusInputLayout);

        String[] categories = getResources().getStringArray(R.array.categories);
        String[] regions = getResources().getStringArray(R.array.region);
        String[] productStatus = getResources().getStringArray(R.array.product_status);


        // Setup the AutoCompleteTextViews with background, rounded corners, and adapter
        setupAutoCompleteTextView(categorySpinner, categoryInputLayout, categories);
        setupAutoCompleteTextView(regionSpinner, regionInputLayout, regions);
        setupAutoCompleteTextView(productStatusSpinner, productStatusInputLayout, productStatus);

//
//        submitSearchButton.setOnClickListener(v -> {
//            Bundle bundle = new Bundle();
//            if (categorySpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.categories)[0])) {
//                bundle.putString("category", "");
//            } else {
//                bundle.putString("category", categorySpinner.getSelectedItem().toString());
//            }
//
//
//            if (subcategorySpinner.getSelectedItem() == null || subcategorySpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.electronics_subcategories)[0])) {
//                bundle.putString("subcategory", "");
//            } else {
//                bundle.putString("subcategory", subcategorySpinner.getSelectedItem().toString());
//            }
//
//            if (regionSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.region)[0])) {
//                bundle.putString("region", "");
//            } else {
//                bundle.putString("region", regionSpinner.getSelectedItem().toString());
//            }
//
//            if (productStatusSpinner.getSelectedItem().toString().equals(getResources().getStringArray(R.array.product_status)[0])) {
//                bundle.putString("productStatus", "");
//            } else {
//                bundle.putString("productStatus", productStatusSpinner.getSelectedItem().toString());
//            }
//            // navigate to the search result fragment and add the bundle
//            Navigation.findNavController(view).navigate(R.id.action_searchProductFragment_to_searchProductResultFragment, bundle);
//        });

        return view;
    }

    // Method to set up the AutoCompleteTextViews
    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, TextInputLayout inputLayout, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapter);

        // Apply the custom background and rounded corners
        autoCompleteTextView.setBackgroundResource(R.drawable.spinner_background);  // Set the custom background

        // Set the threshold to show options immediately
        autoCompleteTextView.setThreshold(1);  // Show the dropdown with one character typed

        // Disable typing by the user
        autoCompleteTextView.setFocusable(false);  // Disables typing
        autoCompleteTextView.setClickable(true);   // Ensures clickability to open the dropdown

        // Ensure that the spinner cannot be typed into
        autoCompleteTextView.setFocusableInTouchMode(false); // Disables focus in touch mode, no typing at all
        autoCompleteTextView.setInputType(InputType.TYPE_NULL);  // Completely disables typing

        // Make sure the TextInputLayout is properly styled
        inputLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        inputLayout.setBoxStrokeColor(getResources().getColor(R.color.blue_primary));  // Set the border color to blue

        // Set up hint and item click listener
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            if (position >= 0) {
                // Update the selected value in the text box
                autoCompleteTextView.setText(items[position]);  // Set the selected value in the text field
                inputLayout.setBoxStrokeColor(getResources().getColor(R.color.blue_primary));  // Ensure the border stays blue after selection
                autoCompleteTextView.clearFocus();  // Remove focus after selection

                // Change hint color to blue after selection
                inputLayout.setHintTextColor(ContextCompat.getColorStateList(requireContext(), R.color.blue_primary));  // Corrected to use getColorStateList
            }
        });

        // Add an onClickListener to open the dropdown when clicked
        autoCompleteTextView.setOnClickListener(v -> {
            // Refresh the adapter to ensure it shows all options
            ArrayAdapter<String> newAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
            autoCompleteTextView.setAdapter(newAdapter);
            autoCompleteTextView.showDropDown();  // Show the dropdown when clicked, even after selection
        });

        // Open the dropdown menu as soon as the AutoCompleteTextView is clicked
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                inputLayout.setBoxStrokeColor(getResources().getColor(R.color.blue_primary));  // Ensure the stroke color remains blue when focused
                autoCompleteTextView.showDropDown(); // Show the dropdown immediately
                inputLayout.setHintTextColor(ContextCompat.getColorStateList(requireContext(), R.color.blue_primary));  // Corrected to use getColorStateList
            } else {
                inputLayout.setBoxStrokeColor(getResources().getColor(R.color.blue_primary));  // Keep the stroke color blue after losing focus
            }
        });
    }
//
//    private void setupSpinners() {
//        // Set up the spinners with default values
//        setUpSpinner(categorySpinner, R.array.categories);
//        setUpSpinner(regionSpinner, R.array.region);
//        setUpSpinner(productStatusSpinner, R.array.product_status);
//
//        // make the subcategorySpinner unclickable until a category is selected
//        subcategorySpinner.setEnabled(false);
//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                subcategorySpinner.setEnabled(position != 0);
//                if (position != 0) {
//                    subcategorySpinner.setEnabled(true);
//                    switch (position) {
//                        case 1:
//                            setUpSpinner(subcategorySpinner, R.array.electronics_subcategories);
//                            break;
//                        case 2:
//                            setUpSpinner(subcategorySpinner, R.array.fashion_subcategories);
//                            break;
//                        case 3:
//                            setUpSpinner(subcategorySpinner, R.array.books_subcategories);
//                            break;
//                        case 4:
//                            setUpSpinner(subcategorySpinner, R.array.home_subcategories);
//                            break;
//                        case 5:
//                            setUpSpinner(subcategorySpinner, R.array.toys_subcategories);
//                        case 6:
//                            setUpSpinner(subcategorySpinner, R.array.other_subcategories);
//                            subcategorySpinner.setEnabled(false);
//                            break;
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                subcategorySpinner.setEnabled(false);
//            }
//        });
//    }

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