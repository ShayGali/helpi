package com.sibi.helpi.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.sibi.helpi.R;

public class SearchPostableFragment extends Fragment {

    private AutoCompleteTextView categorySpinner, subcategorySpinner, regionSpinner, productStatusSpinner;
    private TextInputLayout categoryInputLayout, subcategoryInputLayout, regionInputLayout, productStatusInputLayout;
    private Button submitSearchButton;

    public SearchPostableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_postable, container, false);
        submitSearchButton = view.findViewById(R.id.btnSearchProduct);

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
        String[] productStatus = getResources().getStringArray(R.array.product_condition);

        // Setup the AutoCompleteTextViews with background, rounded corners, and adapter
        setupAutoCompleteTextView(categorySpinner, categoryInputLayout, categories);
        setupAutoCompleteTextView(regionSpinner, regionInputLayout, regions);
        setupAutoCompleteTextView(productStatusSpinner, productStatusInputLayout, productStatus);

        // Disable subcategory spinner initially
        setSpinnerEnabled(subcategorySpinner, false);

        // Set up the subcategory spinner based on the selected category
        setupCategorySpinner();

        submitSearchButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("category", categorySpinner.getText().toString());
            bundle.putString("subcategory", subcategorySpinner.getText().toString());
            bundle.putString("region", regionSpinner.getText().toString());
            bundle.putString("productStatus", productStatusSpinner.getText().toString());

            // navigate to the search result fragment and add the bundle
            Navigation.findNavController(view).navigate(R.id.action_searchPostableFragment_to_searchPostableResultFragment, bundle);
        });

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

    private void setupCategorySpinner() {
        categorySpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCategory = categorySpinner.getText().toString();
            int subcategoryArrayId = getSubcategoryArrayId(selectedCategory);
            if (subcategoryArrayId != 0) {
                String[] subcategories = getResources().getStringArray(subcategoryArrayId);
                setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, subcategories);
                setSpinnerEnabled(subcategorySpinner, true);
            } else {
                setSpinnerEnabled(subcategorySpinner, false);
            }
        });
    }

    private int getSubcategoryArrayId(String category) {
        switch (category) {
            case "Electronics":
                return R.array.electronics_subcategories;
            case "Fashion":
                return R.array.fashion_subcategories;
            case "Books":
                return R.array.books_subcategories;
            case "Home":
                return R.array.home_subcategories;
            case "Toys":
                return R.array.toys_subcategories;
            case "Other":
                return R.array.other_subcategories;
            default:
                return 0;
        }
    }

    private void setSpinnerEnabled(AutoCompleteTextView spinner, boolean enabled) {
        spinner.setEnabled(enabled);
        if (enabled) {
            spinner.setBackgroundResource(R.drawable.spinner_background);
            spinner.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            spinner.setBackgroundResource(R.drawable.spinner_disabled_background);
            spinner.setTextColor(ContextCompat.getColor(requireContext(), R.color.gray));
        }
    }
}