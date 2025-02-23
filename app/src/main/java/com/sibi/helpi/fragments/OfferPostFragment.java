package com.sibi.helpi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.LocationPickerDialogFragment;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.ServicePost;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.LocationUtil;
import com.sibi.helpi.viewmodels.OfferPostViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OfferPostFragment extends Fragment {

    private static final int PICK_IMAGES_REQUEST = 1;

    private OfferPostViewModel offerPostViewModel;
    private AutoCompleteTextView categorySpinner;
    private AutoCompleteTextView subcategorySpinner;
    private AutoCompleteTextView regionSpinner;
    private AutoCompleteTextView typeSpinner;
    private AutoCompleteTextView conditionSpinner;

    private TextInputLayout spinnerPostTypeLayout, categoryInputLayout, subcategoryInputLayout, regionInputLayout, spinnerProductConditionLayout;
    private Button btnUploadImage;
    private ViewPager2 imageSlider;
    private ImageSliderAdapter imageAdapter;
    private ArrayList<Uri> selectedImages;
    private EditText etTitle;
    private EditText etDescription;
    private FloatingActionButton btnPost;
    private FloatingActionButton btnCancelPost;
    private GeoPoint selectedLocation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedImages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_postable, container, false);

        initializeViews(view);
        setupSpinners();
        setupImagePicker();

        getParentFragmentManager().setFragmentResultListener("requestKey", this, (requestKey, bundle) -> {
            double latitude = bundle.getDouble("latitude");
            double longitude = bundle.getDouble("longitude");
            selectedLocation = new GeoPoint(latitude, longitude);
            // display the location name under the region spinner
            regionSpinner.setText(LocationUtil.getLocationNameFromLocation(requireContext(), selectedLocation));
        });


        return view;
    }


    private void initializeViews(View view) {
        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        typeSpinner = view.findViewById(R.id.spinnerPostType);
        conditionSpinner = view.findViewById(R.id.spinnerProductCondition);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageSlider = view.findViewById(R.id.imageSlider);
        btnPost = view.findViewById(R.id.btnPostProduct);
        btnCancelPost = view.findViewById(R.id.btnCancelPost);
        etTitle = view.findViewById(R.id.postTitle);
        etDescription = view.findViewById(R.id.description);

        imageAdapter = new ImageSliderAdapter(requireContext());
        imageSlider.setAdapter(imageAdapter);
        categoryInputLayout = view.findViewById(R.id.categoryInputLayout);
        subcategoryInputLayout = view.findViewById(R.id.subcategoryInputLayout);
        regionInputLayout = view.findViewById(R.id.regionInputLayout);
        spinnerPostTypeLayout = view.findViewById(R.id.spinnerPostTypeLayout);
        spinnerProductConditionLayout = view.findViewById(R.id.spinnerProductConditionLayout);


    }

    private void setupSpinners() {
        setupAutoCompleteTextView(categorySpinner, categoryInputLayout, getResources().getStringArray(R.array.categories));
        setupAutoCompleteTextView(typeSpinner, spinnerPostTypeLayout, getResources().getStringArray(R.array.type));
        setupAutoCompleteTextView(conditionSpinner, spinnerProductConditionLayout, getResources().getStringArray(R.array.product_condition));

        setSubCategoryBlocker();
        setTypeBlocker();

        regionSpinner.setOnClickListener(v -> {
            LocationPickerDialogFragment dialog = new LocationPickerDialogFragment();
            dialog.show(getParentFragmentManager(), "LocationPickerDialogFragment");
        });
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
        typeSpinner.setOnItemClickListener((parent, view, position, id) -> {
            String selectedType = parent.getItemAtPosition(position).toString();
            if ("Service".equalsIgnoreCase(selectedType)) {
                conditionSpinner.setEnabled(false);
                conditionSpinner.setText("");
            } else {
                conditionSpinner.setEnabled(true);
            }
        });
    }

    private void setSubCategoryBlocker() {
        subcategorySpinner.setEnabled(false);
        categorySpinner.setOnItemClickListener((parent, view, position, id) -> {
            subcategorySpinner.setEnabled(position != 0);
            subcategorySpinner.setText("", false);
            if (position != 0) {
                switch (position) {
                    case 1:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.electronics_subcategories));
                        break;
                    case 2:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.fashion_subcategories));
                        break;
                    case 3:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.books_subcategories));
                        break;
                    case 4:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.home_subcategories));
                        break;
                    case 5:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.toys_subcategories));
                        break;
                    case 6:
                        setupAutoCompleteTextView(subcategorySpinner, subcategoryInputLayout, getResources().getStringArray(R.array.other_subcategories));
                        subcategorySpinner.setEnabled(false);
                        break;
                }
            }
        });
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
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    selectedImages.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                selectedImages.add(imageUri);
            }

            imageAdapter.setImages(selectedImages);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        offerPostViewModel = new ViewModelProvider(this).get(OfferPostViewModel.class);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnPost.setOnClickListener(v -> {
            byte[][] images = getImagesData(selectedImages);
            if (validateInput(images)) {
                Postable post = createConcretePost(userViewModel.getCurrentUserId());
                AppConstants.PostType postType;
                if (typeSpinner.getText().toString().equals(getString(R.string.product))) {
                    postType = AppConstants.PostType.PRODUCT;
                } else {
                    postType = AppConstants.PostType.SERVICE;
                }
                offerPostViewModel.savePost(post, images, postType);
            }
        });

        btnCancelPost.setOnClickListener(v -> clearForm());

        observePostProductLiveData();
    }

    private Postable createConcretePost(String userId) {
        String title = etTitle.getText().toString();
        String description = etDescription.getText().toString();
        String category = categorySpinner.getText().toString();
        String subCategory = subcategorySpinner.getText().toString();
        String condition = conditionSpinner.getText().toString();

        // Check if a location is needed and prevent a crash
        if (selectedLocation == null) {
            showToast("Please select a location.");
            return null; // Avoid crashing by returning null
        }

        if (typeSpinner.getText().toString().equals(getString(R.string.product))) {
            return new ProductPost(title, description, category, subCategory, selectedLocation, condition, userId);
        } else if (typeSpinner.getText().toString().equals(getString(R.string.service))) {
            return new ServicePost(title, description, category, subCategory, selectedLocation, userId, condition);
        } else {
            showToast("Invalid post type selected.");
            return null;
        }
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

    private boolean validateInput(byte[][] images) {
        if (typeSpinner.getText().toString().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (categorySpinner.getText().toString().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        // If the category is not "Other", check if the subcategory is filled
        if (!categorySpinner.getText().toString().equals("Other") && subcategorySpinner.getText().toString().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (selectedLocation == null) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (typeSpinner.getText().toString().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (conditionSpinner.getText().toString().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }

        for (byte[] imageData : images) {
            if (imageData.length > ImagesRepository.MAX_FILE_SIZE) {
                showToast(getString(R.string.imgTooLarge));
                return false;
            }
        }
        return true;
    }

    private void observePostProductLiveData() {
        offerPostViewModel.getPostLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.getStatus()) {
                case LOADING:
                    showToast(getString(R.string.uploadingPost));
                    break;

                case SUCCESS:
                    showToast(getString(R.string.postedSuccessfully));
                    clearForm();
                    Navigation.findNavController(requireView()).navigate(R.id.action_offerPostFragment_to_homeFragment);
                    break;

                case ERROR:
                    showToast(getString(R.string.errorPosting));
                    break;
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void clearForm() {
        etDescription.setText("");
        selectedImages.clear();
        imageAdapter.setImages(selectedImages);
        categorySpinner.setText("");
        subcategorySpinner.setText("");
        selectedLocation = null;
        conditionSpinner.setText("");
    }
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

