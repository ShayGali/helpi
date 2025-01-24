package com.sibi.helpi.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.ServicePost;
import com.sibi.helpi.repositories.ImagesRepository;
import com.sibi.helpi.viewmodels.OfferPostViewModel;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class OfferPostFragment extends Fragment {

    private OfferPostViewModel offerPostViewModel;
    private static final int PICK_IMAGES_REQUEST = 1;
    private Spinner categorySpinner;
    private Spinner subcategorySpinner;
    private Spinner regionSpinner;
    private Spinner typeSpinner;
    private Spinner conditionSpinner;
    private Button btnUploadImage;
    private ViewPager2 imageSlider;
    private ImageSliderAdapter imageAdapter;
    private ArrayList<Uri> selectedImages;
    private EditText etDescription;
    private FloatingActionButton btnPost;
    private FloatingActionButton btnCancelPost;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.selectedImages = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer_post, container, false);

        initializeViews(view);
        setupSpinners();
        setupImagePicker();

        return view;
    }

    private void initializeViews(View view) {
        // Assign views to variables (means to get the views from the layout file)
        categorySpinner = view.findViewById(R.id.spinnerCategories);
        subcategorySpinner = view.findViewById(R.id.spinnerSubCategory);
        regionSpinner = view.findViewById(R.id.spinnerRegion);
        typeSpinner = view.findViewById(R.id.spinnerPostType);
        conditionSpinner = view.findViewById(R.id.spinnerProductCondition);
        btnUploadImage = view.findViewById(R.id.btnUploadImage);
        imageSlider = view.findViewById(R.id.imageSlider);
        btnPost = view.findViewById(R.id.btnPostProduct);
        btnCancelPost = view.findViewById(R.id.btnCancelPost);
        etDescription = view.findViewById(R.id.description);

        // Initialize ViewPager2 adapter (means to set the adapter to the ViewPager2)
        imageAdapter = new ImageSliderAdapter(requireContext());
        imageSlider.setAdapter(imageAdapter);
    }

    private void setupSpinners() {

        setUpSpinner(categorySpinner, R.array.categories);
        setUpSpinner(regionSpinner, R.array.region);
        setUpSpinner(typeSpinner, R.array.type);
        setUpSpinner(conditionSpinner, R.array.product_condition);

        setSubCategoryBlocker();
        setTypeBlocker();
    }

    /**
     * This method blocks the condition spinner until a type is selected.
     * If the type is service, the condition spinner is disabled.
     */
    private void setTypeBlocker() {
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = parent.getItemAtPosition(position).toString();
                if ("Service".equalsIgnoreCase(selectedType)) {
                    conditionSpinner.setEnabled(false);
                    conditionSpinner.setSelection(0);
                } else {
                    conditionSpinner.setEnabled(true);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                conditionSpinner.setEnabled(false);
            }
        });
    }

    /**
     * This method blocks the subcategory spinner until a category is selected
     */
    private void setSubCategoryBlocker() {
        subcategorySpinner.setEnabled(false);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subcategorySpinner.setEnabled(position != 0);
                if (position != 0) {
                    subcategorySpinner.setEnabled(true);
                    switch (position) {
                        case 1:
                            setUpSpinner(subcategorySpinner, R.array.electronics_subcategories);
                            break;
                        case 2:
                            setUpSpinner(subcategorySpinner, R.array.fashion_subcategories);
                            break;
                        case 3:
                            setUpSpinner(subcategorySpinner, R.array.books_subcategories);
                            break;
                        case 4:
                            setUpSpinner(subcategorySpinner, R.array.home_subcategories);
                            break;
                        case 5:
                            setUpSpinner(subcategorySpinner, R.array.toys_subcategories);
                        case 6:
                            setUpSpinner(subcategorySpinner, R.array.other_subcategories);
                            subcategorySpinner.setEnabled(false);
                            break;
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                subcategorySpinner.setEnabled(false);

            }
        });
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

        offerPostViewModel = new ViewModelProvider(this).get(OfferPostViewModel.class);

        UserViewModel userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        btnPost.setOnClickListener(v -> {  // onClickListener for the post product button:
            byte[][] images = getImagesData(selectedImages);
            if (validateInput(images)) {
                Postable post = createConcretePost(userViewModel.getUserId());
                offerPostViewModel.savePost(post, images);
            }
        });

        btnCancelPost.setOnClickListener(v -> {
            clearForm();
        });

        observePostProductLiveData();  // Observe the LiveData returned by the ViewModel
    }

    private Postable createConcretePost(String userId) {
        Postable post = typeSpinner.getSelectedItem().toString().equalsIgnoreCase("Service") ?
                new ServicePost(etDescription.getText().toString(), categorySpinner.getSelectedItem().toString(),
                        subcategorySpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(),
                        userId, conditionSpinner.getSelectedItem().toString()) :
                new ProductPost(etDescription.getText().toString(), categorySpinner.getSelectedItem().toString(),
                        subcategorySpinner.getSelectedItem().toString(), regionSpinner.getSelectedItem().toString(),
                        conditionSpinner.getSelectedItem().toString(), userId);
        return post;
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
        if (etDescription.getText().toString().trim().isEmpty()) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (categorySpinner.getSelectedItemPosition() == 0) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (subcategorySpinner.getSelectedItemPosition() == 0) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (regionSpinner.getSelectedItemPosition() == 0) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (typeSpinner.getSelectedItemPosition() == 0) {
            showToast(getString(R.string.allFieldsRequired));
            return false;
        }
        if (conditionSpinner.getSelectedItemPosition() == 0) {
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
        categorySpinner.setSelection(0);
        subcategorySpinner.setSelection(0);
        regionSpinner.setSelection(0);
        conditionSpinner.setSelection(0);
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