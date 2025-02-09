//package com.sibi.helpi;
//
//import android.os.Bundle;
//
//import androidx.fragment.app.Fragment;
//import androidx.viewpager2.widget.ViewPager2;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.sibi.helpi.adapters.ImageSliderAdapter;
//
//
//public class ProductPageFragment extends Fragment {
//
//
//    public ProductPageFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_product_page, container, false);
//        int[] imageResIds = new int[]{
//                R.drawable.red_balloon,
//                R.drawable.blue_balon
//        };
//
//        // Convert resource IDs to URI strings
//        String[] imageUrls = new String[imageResIds.length];
//        for (int i = 0; i < imageResIds.length; i++) {
//            imageUrls[i] = "android.resource://" + getContext().getPackageName() + "/" + imageResIds[i];
//        }
//
//        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls);
//
//        ViewPager2 viewPager = view.findViewById(R.id.imageSlider);
//        viewPager.setAdapter(adapter);
//        return view;
//    }
//}

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

public class PostablePageFragment extends Fragment {

    private
    ViewPager2 productImages;
    private TextView productTitle;
    private TextView productCategory;
    private TextView productSubCategory;
    private TextView productRegion;
    private TextView productStatus;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postable_page, container, false);

        productImages = view.findViewById(R.id.imageSlider);

//        productTitle = view.findViewById(R.id.productTitle);
//        productCategory = view.findViewById(R.id.productCategory);
//        productSubCategory = view.findViewById(R.id.productSubCategory);
//        productRegion = view.findViewById(R.id.productRegion);
//        productStatus = view.findViewById(R.id.productStatus);
        TextView productDescription = view.findViewById(R.id.productDescription);

//        if (getArguments() != null) {
//            productTitle.setText(getArguments().getString("productTitle"));
//            productCategory.setText(getArguments().getString("productCategory"));
//            productSubCategory.setText(getArguments().getString("productSubCategory"));
//            productRegion.setText(getArguments().getString("productRegion"));
//            productStatus.setText(getArguments().getString("productStatus"));
//            productImage.setImageResource(getArguments().getInt("productImageResourceId"));
//        }
//        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls);

        int[] imageResIds = new int[]{
                R.drawable.red_balloon,
                R.drawable.blue_balon
        };

        // Convert resource IDs to URI strings
        String[] imageUrls = new String[imageResIds.length];
        for (int i = 0; i < imageResIds.length; i++) {
            imageUrls[i] = "android.resource://" + getContext().getPackageName() + "/" + imageResIds[i];
        }

        ImageSliderAdapter adapter = new ImageSliderAdapter(getContext(), imageUrls);

        productImages.setAdapter(adapter);

        return view;
    }
}
