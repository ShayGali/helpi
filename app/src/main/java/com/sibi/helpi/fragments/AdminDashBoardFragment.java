package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.ProductSliderAdapter;
import com.sibi.helpi.adapters.ReportAdapter;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.viewmodels.AdminDashBoardViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminDashBoardFragment extends Fragment {

    private RecyclerView reportsRecyclerView;
    private RecyclerView postsRecyclerView;
    private ReportAdapter reportAdapter;
    private ProductSliderAdapter productSliderAdapter;
    private List<Report> reportList;
    private List<ProductPost> postList;
    private AdminDashBoardViewModel adminDashBoardViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postsRecyclerView = view.findViewById(R.id.postsToApproveRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapters
        reportAdapter = new ReportAdapter(new ArrayList<>());
        productSliderAdapter = new ProductSliderAdapter();

        reportsRecyclerView.setAdapter(reportAdapter);
        postsRecyclerView.setAdapter(productSliderAdapter);

        setupViews(view);
        setupObservers();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashBoardViewModel = new ViewModelProvider(requireActivity()).get(AdminDashBoardViewModel.class);
    }

    public void setupViews(View view) {
        // Implement view setup if needed
    }

    public void setupObservers() {
        adminDashBoardViewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
            if (reports != null) {
                reportList = reports;
                reportAdapter.setReportList(reportList);
            }
        });

        adminDashBoardViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postList = new ArrayList<>(posts);
                productSliderAdapter.setProductList(postList);

                // Fetch images for each product
                for (ProductPost productPost : postList) {
                    adminDashBoardViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                        productPost.setImageUrls(imageUrls);
                        productSliderAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }
}