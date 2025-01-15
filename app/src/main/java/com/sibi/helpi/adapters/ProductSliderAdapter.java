package com.sibi.helpi.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sibi.helpi.R;
import com.sibi.helpi.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductSliderAdapter extends RecyclerView.Adapter<ProductSliderAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnItemClickListener onItemClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }

    public ProductSliderAdapter(OnItemClickListener onItemClickListener) {
        this.productList = new ArrayList<>();
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
        Product product = productList.get(position);
        holder.productTitle.setText(product.getDescription());
        holder.productCategory.setText(product.getCategory());
        holder.productSubCategory.setText(product.getSubCategory());
        holder.productRegion.setText(product.getRegion());
        holder.productStatus.setText(product.getCondition());
//        holder.productImage.setImageResource(product.getImageResourceId());

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productTitle;
        TextView productCategory;
        TextView productSubCategory;
        TextView productRegion;
        TextView productStatus;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.imageView);
            productTitle = itemView.findViewById(R.id.textView6);
            productCategory = itemView.findViewById(R.id.textView7);
            productSubCategory = itemView.findViewById(R.id.textView8);
            productRegion = itemView.findViewById(R.id.textView9);
            productStatus = itemView.findViewById(R.id.textView10);
        }
    }
}