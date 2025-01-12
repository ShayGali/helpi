package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class IdoFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ido, container, false);
        Button btn1 = view.findViewById(R.id.button4);

        btn1.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Button Clicked", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}