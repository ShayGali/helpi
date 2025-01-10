package com.sibi.helpi;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sibi.helpi.adapters.ReportAdapter;
import com.sibi.helpi.models.Report;

import java.util.ArrayList;
import java.util.List;

public class AdminDashBoardFragment extends Fragment {

    private RecyclerView reportsRecyclerView;
    private ReportAdapter reportAdapter;
    private List<Report> reportList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dash_board, container, false);

        reportsRecyclerView = view.findViewById(R.id.reportsRecyclerView);
        reportsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize report list and adapter
        reportList = new ArrayList<>();

        // Add dummy data TODO: replace with actual data
        reportList.add(new Report("Spam", "This is a spam report"));
        reportList.add(new Report("Inappropriate Content", "This content is inappropriate"));
        reportList.add(new Report("Other", "Other reasons for reporting"));

        reportAdapter = new ReportAdapter(reportList);
        reportsRecyclerView.setAdapter(reportAdapter);

        return view;
    }
}