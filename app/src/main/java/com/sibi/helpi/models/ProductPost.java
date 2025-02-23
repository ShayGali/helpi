package com.sibi.helpi.models;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.utils.AppConstants.PostStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a product in the Helpi app.
 * Each product has a name, description, category, subcategory, region, situation, and userId.
 * The userId is the id of the user who created the product.
 */
public class ProductPost implements Postable {
    private String id;
    private String title;
    private String description;
    private String category;
    private String subCategory;
    private GeoPoint location;
    private String condition;
    private String userId;
    private PostStatus status;
    private List<String> imageUrls;


    public ProductPost() {
        // Default constructor required for Firebase
    }

    public ProductPost(String title, String description, String category, String subCategory, GeoPoint location, String condition, String userId) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.location = location;
        this.condition = condition;
        this.userId = userId;
        this.status = PostStatus.UNDER_REVIEW;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;

    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCondition() {
        return condition;
    }

    public void setCondition(String situation) {
        this.condition = situation;
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


    @NonNull
    @Override
    public String toString() {
        return "ProductPost{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", location=" + location +
                ", condition='" + condition + '\'' +
                ", userId='" + userId + '\'' +
                ", status=" + status +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
