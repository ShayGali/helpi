package com.sibi.helpi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.utils.FileHandler;
import com.sibi.helpi.viewmodels.OfferProductViewModel;
import com.sibi.helpi.models.Product;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class OfferProductFragment extends Fragment {

    private OfferProductViewModel offerProductViewModel;  // ViewModel to handle the post product operation
    private static final int PICK_IMAGES_REQUEST = 1;
    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private Spinner regionSpinner;
    private Spinner productConditionSpinner;
    private Button btnUploadImage;
    private ViewPager2 imageSlider;
    private ImageSliderAdapter imageAdapter;
    private ArrayList<Uri> selectedImages;
    private EditText etProductDescription;

    FloatingActionButton btnPostProduct;
    FloatingActionButton btnCancelPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedImages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_product, container, false);

        initializeViews(view);  // Initialize views
        setupSpinners();        // Setup spinners
        setupImagePicker();     // Setup image picker

        return view;
    }

    private void initializeViews(View view) {
        // Assign views to variables (means to get the views from the layout file)
        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        productConditionSpinner = view.findViewById(R.id.spinnerProductCondition);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageSlider = view.findViewById(R.id.imageSlider);
        btnPostProduct = view.findViewById(R.id.btnPostProduct);
        btnCancelPost = view.findViewById(R.id.btnCancelPost);
        etProductDescription = view.findViewById(R.id.description);

        // Initialize ViewPager2 adapter (means to set the adapter to the ViewPager2)
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
        productConditionSpinner.setAdapter(new ArrayAdapter<>(requireContext(),
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the ViewModel:
        offerProductViewModel = new ViewModelProvider(this).get(OfferProductViewModel.class);

        // Set an onClickListener for the post product button:
        btnPostProduct.setOnClickListener(v -> {
            // Add validation check here
            if (validateInput()) {  // <-- Add this check
                String description = this.etProductDescription.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();
                String subCategory = subcategorySpinner.getSelectedItem().toString();
                String region = regionSpinner.getSelectedItem().toString();
                String condition = productConditionSpinner.getSelectedItem().toString();
                String userId = UserViewModel.getInstance().getUUID();

                Product product = new Product(description, category, subCategory, region, condition, userId);
                byte[][] images = getImagesData(selectedImages);

                // check if all the images are in the correct size
                for (byte[] imageData : images) {
                    if (imageData.length > ImagesRepository.MAX_FILE_SIZE) {
                        showToast("Image data cannot be greater than 0.5 MB");
                        return;
                    }
                }

                offerProductViewModel.postProduct(product, images);
            }
        });

        observePostProductLiveData();  // Observe the LiveData returned by the ViewModel
    }

    private byte[][] getImagesData(ArrayList<Uri> selectedImages) {
        byte[][] images = new byte[selectedImages.size()][];
        for (int i = 0; i < selectedImages.size(); i++) {
            try {
                Bitmap compressedImage = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImages.get(i));
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                compressedImage.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                images[i] = baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return images;
    }

    private boolean validateInput() {
        if (etProductDescription.getText().toString().trim().isEmpty()) {
            etProductDescription.setError("Description is required");
            return false;
        }
//        if (selectedImages.isEmpty()) {
//            showErrorMessage("Please select at least one image");
//            return false;
//        }
        return true;
    }

    private void observePostProductLiveData() {
        offerProductViewModel.getPostProductLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    showToast("Uploading product...");
                    // You could also show a progress dialog here
                    break;

                case SUCCESS:
                    showToast("Product posted successfully!");
                    String productId = resource.getData();
                    clearForm();
                    // You might want to navigate back or to another screen
                    // getParentFragmentManager().popBackStack();
                    break;

                case ERROR:
                    showToast("Error: " + resource.getMessage());
                    break;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        etProductDescription.setText("");
        selectedImages.clear();
        imageAdapter.setImages(selectedImages);
        categorySpinner.setSelection(0);
        subcategorySpinner.setSelection(0);
        regionSpinner.setSelection(0);
        productConditionSpinner.setSelection(0);
    }
//    TODO: an offer of how to implement the above methods:
//    {
//        switch (resource.getStatus()) {
//            case LOADING:
//                showLoadingDialog();
//                break;
//            case SUCCESS:
//                hideLoadingDialog();
//                showSuccessMessage("Product posted successfully!");
//                navigateBack();
//                break;
//            case ERROR:
//                hideLoadingDialog();
//                showErrorMessage(resource.getMessage());
//                break;
//        }
//    })

}