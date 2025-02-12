package com.sibi.helpi.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
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
import com.sibi.helpi.models.Pair;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.ProductPost;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.viewmodels.AdminDashBoardViewModel;
import com.sibi.helpi.utils.AppConstants.ReportStatus;

import java.util.ArrayList;
import java.util.List;

public class AdminDashBoardFragment extends Fragment  {

    private ReportAdapter reportAdapter;
    private PostableAdapter postSliderAdapter;
    private List<Report> reportList;
    private List<Postable> postList;
    private AdminDashBoardViewModel adminDashBoardViewModel;

    public AdminDashBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        RecyclerView reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        RecyclerView postsRecyclerView = view.findViewById(R.id.postsToApproveRecyclerView);
        postsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize the adapters
        reportAdapter = new ReportAdapter( pair -> {
            Bundle reportBundle = new Bundle();
            reportBundle.putSerializable("report", pair.getFirst());
            reportBundle.putSerializable("postable", pair.getSecond());
            Navigation.findNavController(view).navigate(R.id.action_adminDashBoardFragment_to_reportResolveFragment, reportBundle);
        });

        postSliderAdapter = new PostableAdapter(postable -> {
            Bundle productBundle = new Bundle();
            productBundle.putString("sourcePage", "AdminDashBoardFragment");
            productBundle.putSerializable("postable", postable);
            Navigation.findNavController(view).navigate(R.id.action_adminDashBoardFragment_to_postablePageFragment, productBundle);
        });

        reportsRecyclerView.setAdapter(reportAdapter);
        postsRecyclerView.setAdapter(postSliderAdapter);


        setupObservers();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adminDashBoardViewModel = new ViewModelProvider(requireActivity()).get(AdminDashBoardViewModel.class);
    }



    public void setupObservers() {
        adminDashBoardViewModel.getReports().observe(getViewLifecycleOwner(), reports -> {
            if (reports != null) {
                reportList = new ArrayList<>(reports);
                List<String> postIds = getPostIds(reportList);
                adminDashBoardViewModel.getPosts(postIds).observe(getViewLifecycleOwner(), posts -> {
                    if (posts != null) {
                        List<Pair<Report, Postable>> reportPostPairs = new ArrayList<>();
                        for (Report report : reportList) {
                            for (Postable post : posts) {
                                if (report.getPostId().equals(post.getId())) {
                                    reportPostPairs.add(new Pair<>(report, post));
                                    break;
                                }
                            }
                        }
                        reportAdapter.setReportList(reportPostPairs);
                    }
                });


        }
        });




        adminDashBoardViewModel.getPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts != null) {
                postList = new ArrayList<>(posts);
                postSliderAdapter.setPostableList(postList);

                // Fetch images for each product
                for (Postable productPost : postList) { //TODO - change to postable
                    adminDashBoardViewModel.getProductImages(productPost.getId()).observe(getViewLifecycleOwner(), imageUrls -> {
                        productPost.setImageUrls(imageUrls);
                        postSliderAdapter.notifyDataSetChanged();
                    });
                }
            }
        });
    }




    private List<String> getPostIds(List<Report> reports) {
        List<String> postIds = new ArrayList<>();
        for (Report report : reports) {
            postIds.add(report.getPostId());
        }
        return postIds;
    }
}