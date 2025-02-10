package com.sibi.helpi.models;

import androidx.annotation.NonNull;

import java.util.List;

import com.sibi.helpi.utils.AppConstants.PostStatus;

public class ServicePost implements Postable {
    private String id;
    private String title;
    private String description;
    private String category;
    private String subCategory;
    private String region;
    private String userId;
    private PostStatus status;
    private List<String> imageUrls;

    public ServicePost() {
        // Default constructor required for Firebase
    }

    public ServicePost(String title, String description, String category, String subCategory, String region, String userId, String condition) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.region = region;
        this.userId = userId;
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

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getImageUrls() {
        return imageUrls;
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

    @NonNull
    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", region='" + region + '\'' +
                ", userId='" + userId + '\'' +
                ", imageUrls=" + imageUrls +
                ", postStatus=" + status.getStatusText() +
                '}';
    }
}
