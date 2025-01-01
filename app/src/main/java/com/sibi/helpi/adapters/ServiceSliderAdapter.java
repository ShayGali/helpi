package com.sibi.helpi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sibi.helpi.R;
import com.sibi.helpi.models.Product;
import com.sibi.helpi.models.Service;

import java.util.List;

public class ServiceSliderAdapter extends RecyclerView.Adapter<ServiceSliderAdapter.ProductViewHolder> {

    private List<Service> ServiceList;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(Service service);
    }

    public ServiceSliderAdapter(List<Service> productList, OnItemClickListener onItemClickListener) {
        this.ServiceList = productList;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sreach_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Service service = ServiceList.get(position);


        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(service));
    }

    @Override
    public int getItemCount() {
        return ServiceList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}