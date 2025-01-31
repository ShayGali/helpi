//package com.sibi.helpi.adapters;
//
//import android.annotation.SuppressLint;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.bumptech.glide.Glide;
//import com.sibi.helpi.R;
//import com.sibi.helpi.models.Postable;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PostableAdapter extends RecyclerView.Adapter<PostableAdapter.PostableViewHolder> {
//
//    private List<Postable> postableList;
//    private OnItemClickListener onItemClickListener;
//
//    @SuppressLint("NotifyDataSetChanged")
//    public void setPostableList(List<Postable> postableList) {
//        this.postableList = postableList;
//        notifyDataSetChanged();
//    }
//
//    public interface OnItemClickListener {
//        void onItemClick(Postable postable);
//    }
//
//    public PostableAdapter(OnItemClickListener onItemClickListener) {
//        this.postableList = new ArrayList<>();
//        this.onItemClickListener = onItemClickListener;
//    }
//
//    @NonNull
//    @Override
//    public PostableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_postable, parent, false);
//        return new PostableViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull PostableViewHolder holder, int position) {
//        Postable postable = postableList.get(position);
//        holder.titleTextView.setText(postable.getTitle());
//        holder.categoryTextView.setText(postable.getCategory());
//        holder.subCategoryTextView.setText(postable.getSubCategory());
//        holder.regionTextView.setText(postable.getRegion());
//        holder.statusTextView.setText(postable.getStatus());
//
//        if (!postable.getImageUrls().isEmpty()) {
//            Glide.with(holder.itemView.getContext())
//                    .load(postable.getImageUrls().get(0))
//                    .into(holder.imageView);
//        }
//
//        holder.itemView.setOnClickListener(v -> onItemClickListener.onItemClick(postable));
//    }
//
//    @Override
//    public int getItemCount() {
//        return postableList.size();
//    }
//
//    public static class PostableViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//        TextView titleTextView;
//        TextView categoryTextView;
//        TextView subCategoryTextView;
//        TextView regionTextView;
//        TextView statusTextView;
//
//        public PostableViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.imageView);
//            titleTextView = itemView.findViewById(R.id.textViewTitle);
//            categoryTextView = itemView.findViewById(R.id.textViewCategory);
//            subCategoryTextView = itemView.findViewById(R.id.textViewSubCategory);
//            regionTextView = itemView.findViewById(R.id.textViewRegion);
//            statusTextView = itemView.findViewById(R.id.textViewStatus);
//        }
//    }
//}