package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.helpi.adapters.ServiceSliderAdapter;
import com.sibi.helpi.models.Service;

import java.util.ArrayList;
import java.util.List;

public class SearchServiceResultFragment extends Fragment {

    private RecyclerView serviceRecyclerView;
    private ServiceSliderAdapter serviceSliderAdapter;
    private List<Service> serviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_service_result, container, false);

        serviceRecyclerView = view.findViewById(R.id.serviceRecycleView);
        serviceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize product list and adapter
        serviceList = new ArrayList<>();

        // Add dummy data TODO: replace with actual data
        serviceList.add(new Service());
        serviceList.add(new Service());
        serviceList.add(new Service());

        serviceSliderAdapter = new ServiceSliderAdapter(serviceList, service -> {
            // Handle navigation to product page
            Bundle bundle = new Bundle();
//            Navigation.findNavController(view).navigate(R.id.action_searchServiceResultFragment_to_ServiceFragment, bundle);
        });

        serviceRecyclerView.setAdapter(serviceSliderAdapter);

        return view;
    }
}

