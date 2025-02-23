package com.sibi.helpi.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.LocationPickerDialogFragment;
import com.sibi.helpi.R;
import com.sibi.helpi.utils.LocationUtil;

//TODO- add option do  not have fillter
public class SearchPostableFragment extends Fragment {

    private AutoCompleteTextView postTypeSpinner, categorySpinner, subcategorySpinner, regionSpinner, productStatusSpinner;
    private TextInputLayout postTypeInputLayout, categoryInputLayout, subcategoryInputLayout, regionInputLayout, productStatusInputLayout;
    private View submitSearchButton, clearSearchButton;
    private double latitude, longitude;


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

        initializeViews(view);

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            latitude = bundle.getDouble("latitude");
            longitude = bundle.getDouble("longitude");
            // display the location name under the region spinner
            regionSpinner.setText(LocationUtil.getLocationName(requireContext(), latitude, longitude));
        });

        setupAutoCompleteTextView(categorySpinner, categoryInputLayout, addAllOption(getResources().getStringArray(R.array.categories)));
        setupAutoCompleteTextView(postTypeSpinner, postTypeInputLayout, addAllOption(getResources().getStringArray(R.array.type)));
        setupAutoCompleteTextView(productStatusSpinner, productStatusInputLayout, addAllOption(getResources().getStringArray(R.array.product_condition)));
//        setupAutoCompleteTextView(regionSpinner, regionInputLayout, regions);
        setSubCategoryBlocker();
        setTypeBlocker();

        regionSpinner.setOnClickListener(v -> {
            LocationPickerDialogFragment dialog = new LocationPickerDialogFragment();
            dialog.show(getParentFragmentManager(), "LocationPickerDialogFragment");
        });

        submitSearchButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("category", categorySpinner.getText().toString());
            bundle.putString("subcategory", subcategorySpinner.getText().toString());
            bundle.putString("region", regionSpinner.getText().toString());
            bundle.putString("productStatus", productStatusSpinner.getText().toString());
            bundle.putDouble("latitude", latitude);
            bundle.putDouble("longitude", longitude);

            if (postTypeSpinner.getText().toString().equals("Service")) {
                bundle.putString("postType", "Service");
            } else if( postTypeSpinner.getText().toString().equals("Product")) {
                bundle.putString("postType", "Product");
            }
            else {
                bundle.putString("postType", "Any");
            }
            // navigate to the search result fragment and add the bundle
            Navigation.findNavController(view).navigate(R.id.action_searchPostableFragment_to_searchPostableResultFragment, bundle);
        });

        clearSearchButton.setOnClickListener(v -> {
            // Clear the spinner items to the default value
            postTypeSpinner.setText("", false);
            categorySpinner.setText("", false);
            subcategorySpinner.setText("", false);
            regionSpinner.setText("", false);
            productStatusSpinner.setText("", false);
        });

        return view;
    }

    private void initializeViews(View view) {
        submitSearchButton = view.findViewById(R.id.btnSearch);
        clearSearchButton = view.findViewById(R.id.btnClearFilters);

        categorySpinner = view.findViewById(R.id.categorySpinner);
        subcategorySpinner = view.findViewById(R.id.subcategorySpinner);
        regionSpinner = view.findViewById(R.id.regionSpinner);
        productStatusSpinner = view.findViewById(R.id.productStatusSpinner);

        categoryInputLayout = view.findViewById(R.id.categoryInputLayout);
        subcategoryInputLayout = view.findViewById(R.id.subcategoryInputLayout);
        regionInputLayout = view.findViewById(R.id.regionInputLayout);
        productStatusInputLayout = view.findViewById(R.id.productStatusInputLayout);

        postTypeSpinner = view.findViewById(R.id.postTypeSpinner);
        postTypeInputLayout = view.findViewById(R.id.postTypeInputLayout);
    }

    private void setupAutoCompleteTextView(AutoCompleteTextView autoCompleteTextView, TextInputLayout inputLayout, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, items);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setKeyListener(null); // Prevent manual input
        autoCompleteTextView.setThreshold(1000); // Prevents typing to trigger suggestions

        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
//            autoCompleteTextView.setText(items[position], false); // Ensures selection from list
            inputLayout.setBoxStrokeColor(ContextCompat.getColor(requireContext(), R.color.blue_primary));
            inputLayout.setHintTextColor(ContextCompat.getColorStateList(requireContext(), R.color.blue_primary));
        });

        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown(); // Show dropdown when focused
            }
        });

        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown()); // Open dropdown on click
    }

    private void setTypeBlocker() {
        postTypeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = parent.getItemAtPosition(position).toString();
            if ("Service".equalsIgnoreCase(selectedType)) {
                productStatusSpinner.setEnabled(false);
                productStatusSpinner.setText("");
            } else {
                productStatusSpinner.setEnabled(true);
            }
        });
    }

    private void setSubCategoryBlocker() {
        subcategorySpinner.setEnabled(false);
        categorySpinner.setOnItemClickListener((parent, view, position, id) -> {
            subcategorySpinner.setEnabled(position != 0);
            if (position != 0) {
                String[] subcategories = null;
                switch (position) {
                    case 1:
                        subcategories = getResources().getStringArray(R.array.electronics_subcategories);
                        break;
                    case 2:
                        subcategories = getResources().getStringArray(R.array.fashion_subcategories);
                        break;
                    case 3:
                        subcategories = getResources().getStringArray(R.array.books_subcategories);
                        break;
                    case 4:
                        subcategories = getResources().getStringArray(R.array.home_subcategories);
                        break;
                    case 5:
                        subcategories = getResources().getStringArray(R.array.toys_subcategories);
                        break;
                    case 6:
                        subcategories = getResources().getStringArray(R.array.other_subcategories);
                        subcategorySpinner.setEnabled(false);
                        break;
                    default:
                        subcategories = new String[]{};
                        subcategorySpinner.setEnabled(false);
                        break;
                }
                String[] subcategoriesWithAll = addAllOption(subcategories);

                setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, subcategoriesWithAll);
                subcategorySpinner.setText("", false);
            }
        });
    }

    private String[] addAllOption(String[] array) {
        String[] newArray = new String[array.length + 1];
        newArray[array.length] = getString(R.string.find_evrthng);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

}