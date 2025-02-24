package com.sibi.helpi.adapters;

import static com.sibi.helpi.utils.LocaleHelper.getTranslatedCategory;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.sibi.helpi.R;
import com.sibi.helpi.models.Postable;
import com.sibi.helpi.models.Report;
import com.sibi.helpi.models.Pair;
import com.sibi.helpi.utils.AppConstants;
import com.sibi.helpi.utils.LocaleHelper.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportViewHolder> {

    private final List<Pair<Report, Postable>> reportList;

    private final OnItemClickListener onItemClickListener;


    public ReportAdapter( OnItemClickListener onItemClickListener) {
        this.reportList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Pair<Report, Postable> report = reportList.get(position);
        Report report1 = report.getFirst();
        Postable postable = report.getSecond();
        String reportReason = getTranslatedCategory(holder.itemView.getContext(), report1.getReason().getReasonText(), "reportReason", AppConstants.ENGLISH_TO_LOCAL);
        reportReason = holder.itemView.getContext().getString(R.string.report_reason) +" "+  reportReason;
        String reportDescription = holder.itemView.getContext().getString(R.string.report_description) + " " +report1.getReporterNotes();
        String postTitle = holder.itemView.getContext().getString(R.string.post_title) + " " + postable.getTitle();
        String postDescription = holder.itemView.getContext().getString(R.string.post_description) + " " + postable.getDescription();

        holder.reasonTextView.setText(reportReason);
        holder.descriptionTextView.setText(reportDescription);
        holder.postTitleTextView.setText(postTitle);
        holder.postDescriptionTextView.setText(postDescription);
        if (!postable.getImageUrls().isEmpty()) {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.noImageTextView.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(postable.getImageUrls().get(0))
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
            holder.noImageTextView.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            onItemClickListener.onItemClick(report);
        });

    }

    @Override
    public int getItemCount() {
        if (reportList == null) {
            return 0;
        }
        return reportList.size();
    }

    public void setReportList(List<Pair<Report, Postable>> reportList) {
        this.reportList.clear();
         this.reportList.addAll(reportList);
        notifyDataSetChanged();

    }





    public static class ReportViewHolder extends RecyclerView.ViewHolder {
        TextView reasonTextView;
        TextView descriptionTextView;
        TextView postTitleTextView;
        TextView postDescriptionTextView;
        ImageView imageView;
        TextView noImageTextView;







        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reasonTextView = itemView.findViewById(R.id.tvReportReason);
            descriptionTextView = itemView.findViewById(R.id.tvReportDescription);
            postTitleTextView = itemView.findViewById(R.id.tvPostTitle);
            postDescriptionTextView = itemView.findViewById(R.id.tvPostDescription);
            imageView = itemView.findViewById(R.id.imageViewReport);
            noImageTextView = itemView.findViewById(R.id.tvNoImage);


        }
    }


    public interface OnItemClickListener {
        void onItemClick(Pair<Report, Postable> report);
    }
}