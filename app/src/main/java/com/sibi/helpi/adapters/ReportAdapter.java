package com.sibi.helpi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sibi.helpi.R;
import com.sibi.helpi.models.Report;

import java.util.List;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Report> reportList;
    private final OnReportActionListener onReportActionListener;

    public ReportAdapter(List<Report> reportList, OnReportActionListener onReportActionListener) {
        this.reportList = reportList;
        this.onReportActionListener = onReportActionListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report report = reportList.get(position);
        holder.reasonTextView.setText(report.getReason().toString());
        holder.descriptionTextView.setText(report.getReporterNotes());
        holder.deleteButton.setOnClickListener(v -> onReportActionListener.onDeleteReport(report.getReportId()));
        holder.rejectButton.setOnClickListener(v -> onReportActionListener.onRejectReport(report.getReportId()));
    }

    @Override
    public int getItemCount() {
        if (reportList == null) {
            return 0;
        }
        return reportList.size();
    }

    public void setReportList(List<Report> reportList) {
        this.reportList.clear();
        this.reportList.addAll(reportList);
        notifyDataSetChanged();
    }
    public void removeReport(String reportId) {
        for (Report report : reportList) {
            if (report.getReportId().equals(reportId)) {
                reportList.remove(report);
                notifyDataSetChanged();
                break;
            }
        }
    }

    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reasonTextView;
        TextView descriptionTextView;
        Button deleteButton;
        Button rejectButton;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reasonTextView = itemView.findViewById(R.id.textView16);
            descriptionTextView = itemView.findViewById(R.id.textView20);
            deleteButton = itemView.findViewById(R.id.deletePostButton);
            rejectButton = itemView.findViewById(R.id.rejectReportButton);
        }
    }

    public interface OnReportActionListener {
        void onDeleteReport(String reportId);
        void onRejectReport(String reportId);
    }
}