package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.viewmodels.SearchProductViewModel;
import com.sibi.helpi.utils.AppConstants.PostStatus;

public class PostablePageFragment extends Fragment {

    private ViewPager2 productImages;
    private TextView productTitle;
    private TextView productDescription;
    private TextView deliveryPersonName;

    private FloatingActionButton acceptButton;
    private FloatingActionButton rejectButton;
    private Postable postable;

    private SearchProductViewModel postableViewModel;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postableViewModel = new SearchProductViewModel(); //TODO change to singleton
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postable_page, container, false);



        productImages = view.findViewById(R.id.imageSlider);
        productTitle = view.findViewById(R.id.postableTitle);
        productDescription = view.findViewById(R.id.postDescription);
        deliveryPersonName = view.findViewById(R.id.deliveryPersonName);
        acceptButton = view.findViewById(R.id.acceptFab);
        rejectButton = view.findViewById(R.id.rejectFab);

        if(getActivity() != null) {
            assert getArguments() != null;
            String sourcePage= getArguments().getString("sourcePage");
            if(sourcePage != null) {
                if(!sourcePage.equals("AdminDashBoardFragment")) {
                    // Hide the accept and reject buttons
                    view.findViewById(R.id.acceptFab).setVisibility(View.GONE);
                    view.findViewById(R.id.rejectFab).setVisibility(View.GONE);
                }
            }

        }



        if (getArguments() != null) {

            String sourcePage= getArguments().getString("sourcePage");
            if(sourcePage != null) {
                if(!sourcePage.equals("AdminDashBoardFragment")) {
                    // Hide the accept and reject buttons
                    acceptButton.setVisibility(View.GONE);
                    rejectButton.setVisibility(View.GONE);
                }
            }

            postable = (Postable) getArguments().getSerializable("postable");

            if (postable != null) {
                productTitle.setText(postable.getTitle());
                productDescription.setText(postable.getDescription());
//                deliveryPersonName.setText(postable.getDeliveryPersonName()); //TODO - fetch delivery person data from the repository

                // Assuming Postable has a method getImageUrls() that returns a list of image URLs
                String[] imageUrls = postable.getImageUrls().toArray(new String[0]);
                ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls);
                productImages.setAdapter(adapter);
            }
        }
        setupButtons();



        return view;
    }

    private void setupButtons(){
        acceptButton.setOnClickListener(v -> {
            // Accept the postable
            acceptPostable(postable);
        });

        rejectButton.setOnClickListener(v -> {
            // Reject the postable
            rejectPostable(postable);
        });
    }

    private void acceptPostable(Postable post) {
        if(post != null) {
            String postId = post.getId();
            LiveData<Boolean> succeeded = postableViewModel.updatePostStatus(postId, PostStatus.APPROVED);
            succeeded.observe(getViewLifecycleOwner(), isSucceeded -> {
                if(isSucceeded) {
                    Toast.makeText(getContext(), "Post accepted", Toast.LENGTH_SHORT).show();
                    // if the post is accepted, navigate back to the admin dashboard
                    Navigation.findNavController(getView()).navigate(R.id.action_postablePageFragment_to_adminDashBoardFragment);



                } else {
                    Toast.makeText(getContext(), "Failed to accept post, please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void rejectPostable(Postable post) {
        if(post != null) {
            String postId = post.getId();
            LiveData<Boolean> succeeded = postableViewModel.updatePostStatus(postId, PostStatus.REJECTED);
            succeeded.observe(getViewLifecycleOwner(), isSucceeded -> {
                if(isSucceeded) {
                    Toast.makeText(getContext(), "Post rejected", Toast.LENGTH_SHORT).show();
                    // if the post is rejected, navigate back to the admin dashboard
                    Navigation.findNavController(getView()).navigate(R.id.action_postablePageFragment_to_adminDashBoardFragment);
                } else {
                    Toast.makeText(getContext(), "Failed to reject post, please try again later", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}