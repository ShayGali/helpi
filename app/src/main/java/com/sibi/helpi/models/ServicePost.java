package com.sibi.helpi.models;


import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.utils.AppConstants.PostStatus;

public class ServicePost implements Postable {
    private String id;
    private String title;
    private String description;
    private String category;
    private String subCategory;
    private GeoPoint location;
    private String userId;
    private PostStatus status;
    private List<String> imageUrls;
    private Timestamp timestamp;

    public ServicePost() {
        // Default constructor required for Firebase
    }

    public ServicePost(String title, String description, String category, String subCategory, GeoPoint location, String userId, Timestamp timestamp) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.location = location;
        this.userId = userId;
        this.timestamp = timestamp;
        this.status = PostStatus.UNDER_REVIEW;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getImageUrls() {
        return imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @NonNull

    @Override
    public String toString() {
        return "ServicePost{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", location=" + location +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", imageUrls=" + imageUrls +
                ", timestamp=" + timestamp +
                '}';
    }
}

