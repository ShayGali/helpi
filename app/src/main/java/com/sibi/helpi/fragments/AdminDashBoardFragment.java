package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sibi.helpi.R;
import com.sibi.helpi.adapters.PostableAdapter;
import com.sibi.helpi.adapters.ReportAdapter;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.viewmodels.AdminDashBoardViewModel;
import com.sibi.helpi.utils.AppConstants.reportStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminDashBoardFragment extends Fragment implements ReportAdapter.OnReportActionListener {

    private RecyclerView reportsRecyclerView;
    private RecyclerView postsRecyclerView;
    private ReportAdapter reportAdapter;
    private PostableAdapter productSliderAdapter;
    private List<Report> reportList;
    private List<ProductPost> postList;
    private AdminDashBoardViewModel adminDashBoardViewModel;

    public AdminDashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        postsRecyclerView = view.findViewById(R.id.postsToApproveRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapters
        reportAdapter = new ReportAdapter(new ArrayList<>(), this);

        productSliderAdapter = new PostableAdapter(postable -> {
            // todo - check want to do with the postable
            Bundle productBundle = new Bundle();
            productBundle.putString("sourcePage", "AdminDashBoardFragment");
            productBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_adminDashBoardFragment_to_postablePageFragment, productBundle);
        });

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
                productSliderAdapter.setPostableList(postList);

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


    @Override
    public void onDeleteReport(String reportId) {
        adminDashBoardViewModel.updateReport(reportId, reportStatus.RESOLVED).observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Remove the report from the list
                reportAdapter.removeReport(reportId);
                // Notify the adapter that the data has changed
                Toast.makeText(getContext(), "Report resolved", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the failure case
                Toast.makeText(getContext(), "Failed to resolve report", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRejectReport(String reportId) {
        adminDashBoardViewModel.updateReport(reportId, reportStatus.REJECTED).observe(getViewLifecycleOwner(), isSuccess -> {
            if (isSuccess) {
                // Remove the report from the list
                reportAdapter.removeReport(reportId);
                // Notify the adapter that the data has changed
                Toast.makeText(getContext(), "Report rejected", Toast.LENGTH_SHORT).show();
            } else {
                // Handle the failure case
                Toast.makeText(getContext(), "Failed to reject report", Toast.LENGTH_SHORT).show();
            }
        });


    }
}