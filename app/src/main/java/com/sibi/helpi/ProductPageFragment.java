package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.helpi.adapters.ImageSliderAdapter;


public class ProductPageFragment extends Fragment {


    public ProductPageFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_product_page, container, false);
        int[] imageResIds = new int[]{
                R.drawable.red_balloon,
                R.drawable.blue_balon
        };
        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageResIds);

        ViewPager2 viewPager = view.findViewById(R.id.imageSlider);
        viewPager.setAdapter(adapter);
        return view;
    }
}