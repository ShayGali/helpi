package com.sibi.helpi.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sibi.helpi.R;
import com.sibi.helpi.models.Postable;

import java.util.ArrayList;
import java.util.List;

public class PostableAdapter extends RecyclerView.Adapter<PostableAdapter.PostableViewHolder> {

    private List<? extends Postable> postableList;
    private final OnItemClickListener onItemClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setPostableList(List<? extends Postable> postableList) {
        this.postableList = postableList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Postable postable);
    }

    public PostableAdapter(OnItemClickListener onItemClickListener) {
        this.postableList = new ArrayList<>();
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public PostableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postable, parent, false);
        return new PostableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostableViewHolder holder, int position) {
        Postable postable = postableList.get(position);
        holder.titleTextView.setText(postable.getTitle());
        holder.categoryTextView.setText(postable.getCategory());
        holder.subCategoryTextView.setText(postable.getSubCategory());
        holder.regionTextView.setText(postable.getRegion());
        holder.statusTextView.setText(postable.getStatus().getStatusText());

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

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(postable));
    }

    @Override
    public int getItemCount() {
        return postableList.size();
    }

    public static class PostableViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView noImageTextView;
        TextView titleTextView;
        TextView categoryTextView;
        TextView subCategoryTextView;
        TextView regionTextView;
        TextView statusTextView;

        public PostableViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            noImageTextView = itemView.findViewById(R.id.noImageTextView);
            titleTextView = itemView.findViewById(R.id.postableTitle);
            categoryTextView = itemView.findViewById(R.id.postableCategory);
            subCategoryTextView = itemView.findViewById(R.id.postableSubCategory);
            regionTextView = itemView.findViewById(R.id.postableRegion);
            statusTextView = itemView.findViewById(R.id.postableStatus);
        }
    }
}