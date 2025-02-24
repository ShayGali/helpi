package com.sibi.helpi.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.sibi.helpi.MainActivity;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.PostableAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.repositories.PostRepository;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.viewmodels.ProductViewModel;
import com.sibi.helpi.viewmodels.SearchProductViewModel;
import com.sibi.helpi.viewmodels.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FusedLocationProviderClient fusedLocationClient;
    private UserViewModel userViewModel;
    private TextView usernameTextView;
    private ImageView profileImage;
    //last product
    private RecyclerView lastProductRecyclerView;

    private LinearLayoutManager productHorizontalLayoutManager;

    private PostableAdapter productSliderAdapter;
    private List<Postable> productPostList;
    private SearchProductViewModel searchProductViewModel;


    //last services

    private RecyclerView lastServicesRecyclerView;

    private LinearLayoutManager serviceHorizontalLayoutManager;

    private PostableAdapter serviceSliderAdapter;
    private List<Postable> servicePostList;
    private SearchProductViewModel searchServiceViewModel;








    private Button goToAdminDashboardButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getCurrentUser();
        searchProductViewModel = new SearchProductViewModel();
        searchServiceViewModel = new SearchProductViewModel();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //---------------------------------------------------last product
        lastProductRecyclerView = view.findViewById(R.id.last_added_products);
        productHorizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        lastProductRecyclerView.setLayoutManager(productHorizontalLayoutManager);

        // get the products from the repository
        LiveData<List<Postable>> postsLiveData = searchProductViewModel.getLatestPosts(AppConstants.PostType.PRODUCT, 5);

        productSliderAdapter = new PostableAdapter(postable -> {
            Bundle postableBundle = new Bundle();
            postableBundle.putString("sourcePage", "SearchPostableResultFragment");
            postableBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_searchPostableResultFragment_to_postablePageFragment, postableBundle);
        });

        lastProductRecyclerView.setAdapter(productSliderAdapter);

        // observe the products
        postsLiveData.observe(getViewLifecycleOwner(), products -> {
            productPostList = new ArrayList<>(products);
            productSliderAdapter.setPostableList(productPostList);

            // Fetch images for each product
            for (Postable productPost : productPostList) {
                searchProductViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                    productPost.setImageUrls(imageUrls);
                    productSliderAdapter.notifyDataSetChanged();
                });
            }
        });



        //---------------------------------------------------last services
        lastServicesRecyclerView = view.findViewById(R.id.last_added_services);
        serviceHorizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        lastServicesRecyclerView.setLayoutManager(serviceHorizontalLayoutManager);


        // get the products from the repository
        LiveData<List<Postable>> serviceLiveData = searchServiceViewModel.getLatestPosts(AppConstants.PostType.PRODUCT, 5);

        serviceSliderAdapter = new PostableAdapter(postable -> {
            Bundle postableBundle = new Bundle();
            postableBundle.putString("sourcePage", "SearchPostableResultFragment");
            postableBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_searchPostableResultFragment_to_postablePageFragment, postableBundle);
        });

        lastServicesRecyclerView.setAdapter(productSliderAdapter);

        // observe the products
        serviceLiveData.observe(getViewLifecycleOwner(), products -> {
            servicePostList = new ArrayList<>(products);
            serviceSliderAdapter.setPostableList(servicePostList);

            // Fetch images for each product
            for (Postable productPost : servicePostList) {
                searchServiceViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                    productPost.setImageUrls(imageUrls);
                    serviceSliderAdapter.notifyDataSetChanged();
                });
            }
        });


//        return view;

        usernameTextView = view.findViewById(R.id.userNameTextView);
        profileImage = view.findViewById(R.id.profile_img);
        goToAdminDashboardButton = view.findViewById(R.id.goToAdminDashboardButton);

        setupViews(view);
        setupObservers();

        return view;
    }

    private void setupViews(View view) {
        profileImage.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_profileFragment)
        );
        goToAdminDashboardButton.setOnClickListener( v -> {
            Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_adminDashBoardFragment);
        });

        goToAdminDashboardButton.setVisibility(View.GONE);

    }

    private void setupObservers() {
        userViewModel.getUserState().observe(getViewLifecycleOwner(), state -> {
            if (state.isLoading()) {
                showLoading();
                Log.d("HomeFragment", "State: Loading");
            } else if (state.getError() != null) {
                hideLoading();
                Log.e("HomeFragment", "State: Error - " + state.getError());
                usernameTextView.setText(R.string.welcome_guest);
                // Optionally show error to user
                // Toast.makeText(requireContext(), state.getError(), Toast.LENGTH_SHORT).show();
            } else if (state.getUser() != null) {
                hideLoading();
                Log.d("HomeFragment", "State: Success - User: " + state.getUser().getEmail());
                String helloMsg = getString(R.string.hi) + ", " + state.getUser().getFirstName() +
                        " " + state.getUser().getLastName() + "!";
                usernameTextView.setText(helloMsg);

                String imageUrl = state.getUser().getProfileImgUri();
                Log.d("HomeFragment", "Profile image URL: " + imageUrl);
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Glide.with(this)
                            .load(imageUrl)
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.icon_account_circle);
                }

                if (state.getUser() != null && state.getUser().isAdmin()) {
                    goToAdminDashboardButton.setVisibility(View.VISIBLE);
                } else {
                    goToAdminDashboardButton.setVisibility(View.GONE);
                }

            } else {
                hideLoading();
                Log.w("HomeFragment", "State: Unknown - neither loading, error, nor user");
                usernameTextView.setText(R.string.welcome_guest);
            }
        });
    }


    private void showLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.showProgressBar();
    }

    private void hideLoading() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.hideProgressBar();
    }
}