package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SearchProductFragment extends Fragment {


    public SearchProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_product, container, false);

        // find the text place holder
        TextView textView = view.findViewById(R.id.textPlaceHolder);

        // navigate to the product page
        textView.setOnClickListener(v -> {
            Navigation.findNavController(view).navigate(R.id.action_searchProductFragment_to_profuctFragment);
        });
        return view;
    }
}