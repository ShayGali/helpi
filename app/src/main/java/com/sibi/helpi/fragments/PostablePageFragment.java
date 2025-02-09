package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ImageSliderAdapter;
import com.sibi.helpi.models.Postable;

public class PostablePageFragment extends Fragment {

    private ViewPager2 productImages;
    private TextView productTitle;
    private TextView productDescription;
    private TextView deliveryPersonName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postable_page, container, false);

        productImages = view.findViewById(R.id.imageSlider);
        productTitle = view.findViewById(R.id.postableTitle);
        productDescription = view.findViewById(R.id.postDescription);
        deliveryPersonName = view.findViewById(R.id.deliveryPersonName);

        if (getArguments() != null) {
            Postable postable = (Postable) getArguments().getSerializable("postable");

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

        return view;
    }
}