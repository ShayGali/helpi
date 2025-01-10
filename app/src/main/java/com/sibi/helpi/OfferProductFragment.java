package com.sibi.helpi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.sibi.helpi.adapters.ImageSliderAdapter;

import java.util.ArrayList;

public class OfferProductFragment extends Fragment {
    private static final int PICK_IMAGES_REQUEST = 1;

    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private Spinner regionSpinner;
    private Spinner productStatusSpinner;
    private Button btnUploadImage;
    private ViewPager2 imageSlider;
    private ImageSliderAdapter imageAdapter;
    private ArrayList<Uri> selectedImages;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedImages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_product, container, false);

        initializeViews(view);
        setupSpinners();
        setupImagePicker();

        return view;
    }

    private void initializeViews(View view) {
        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        productStatusSpinner = view.findViewById(R.id.spinnerProductSituation);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageSlider = view.findViewById(R.id.imageSlider);

        // Initialize ViewPager2 adapter
        imageAdapter = new ImageSliderAdapter(requireContext());
        imageSlider.setAdapter(imageAdapter);
    }

    private void setupSpinners() {
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
    }

    private void setupImagePicker() {
        btnUploadImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Images"), PICK_IMAGES_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGES_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImages.clear();

            if (data.getClipData() != null) {
                // Multiple images selected
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageUri);
                }
            } else if (data.getData() != null) {
                // Single image selected
                Uri imageUri = data.getData();
                selectedImages.add(imageUri);
            }

            imageAdapter.setImages(selectedImages);
        }
    }
}