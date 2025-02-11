package com.sibi.helpi.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.sibi.helpi.R;

import java.util.ArrayList;

public class ImageSliderAdapter extends RecyclerView.Adapter<ImageSliderAdapter.ImageViewHolder> {
    private Context context;
    private String[] imageUrls;

    // Constructor for URLs
    public ImageSliderAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    public ImageSliderAdapter(Context context) {
        this.context = context;
        this.imageUrls = new String[0];
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_slider, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (imageUrls.length == 0) {
            holder.imageView.setVisibility(View.GONE);
            holder.noImageTextView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.VISIBLE);
            holder.noImageTextView.setVisibility(View.GONE);
            Glide.with(context)
                    .load(imageUrls[position])
                    .fitCenter()
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.length == 0 ? 1 : imageUrls.length;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setImages(ArrayList<Uri> imagesUris) {
        this.imageUrls = new String[imagesUris.size()];
        for (int i = 0; i < imagesUris.size(); i++) {
            this.imageUrls[i] = imagesUris.get(i).toString();
        }
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView noImageTextView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sliderImageView);
            noImageTextView = itemView.findViewById(R.id.noImageTextView);
        }
    }
}