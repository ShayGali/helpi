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
import com.sibi.helpi.models.ProductPost;

import java.util.ArrayList;
import java.util.List;

public class ProductSliderAdapter extends RecyclerView.Adapter<ProductSliderAdapter.ProductViewHolder> {

    private List<ProductPost> productPostList;
    private OnItemClickListener onItemClickListener;

    @SuppressLint("NotifyDataSetChanged")
    public void setProductList(List<ProductPost> productPostList) {
        this.productPostList = productPostList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(ProductPost productPost);
    }

    public ProductSliderAdapter() {
        this.productPostList = new ArrayList<>();
    }

    public ProductSliderAdapter(OnItemClickListener onItemClickListener) {
        this.productPostList = new ArrayList<>();
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
        ProductPost productPost = productPostList.get(position);
        holder.productTitle.setText(productPost.getDescription());
        holder.productCategory.setText(productPost.getCategory());
        holder.productSubCategory.setText(productPost.getSubCategory());
        holder.productRegion.setText(productPost.getRegion());
        holder.productStatus.setText(productPost.getCondition());

        if (!productPost.getImageUrls().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(productPost.getImageUrls().get(0))
                    .into(holder.productImage);
        }

        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(productPost));
    }

    @Override
    public int getItemCount() {
        return productPostList.size();
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