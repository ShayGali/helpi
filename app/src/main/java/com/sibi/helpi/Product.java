package com.sibi.helpi;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a product in the Helpi app.
 * Each product has a name, description, category, subcategory, region, situation, and userId.
 * The userId is the id of the user who created the product.
 */
public class Product {
    private String id;
    private String description;
    private String category;
    private String subCategory;
    private String region;
    private String condition;
    private String userId;
    private List<String> imageUrls;

    public Product() {
        // Default constructor required for Firebase
    }

    public Product(String description, String category, String subCategory, String region, String condition, String userId) {
        this.description = description;
        this.category = category;
        this.subCategory = subCategory;
        this.region = region;
        this.condition = condition;
        this.userId = userId;
    }

    // Getters and setters:
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

    // toString method:
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", region='" + region + '\'' +
                ", situation='" + condition + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }


}
