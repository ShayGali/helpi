package com.sibi.helpi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sibi.helpi.R;
import com.sibi.helpi.models.ServicePost;

import java.util.List;

public class ServiceSliderAdapter extends RecyclerView.Adapter<ServiceSliderAdapter.ProductViewHolder> {

    private List<ServicePost> servicePostList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(ServicePost servicePost);
    }

    public ServiceSliderAdapter(List<ServicePost> productList, OnItemClickListener onItemClickListener) {
        this.servicePostList = productList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sreach_service, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ServicePost servicePost = servicePostList.get(position);


        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(servicePost));
    }

    @Override
    public int getItemCount() {
        return servicePostList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}