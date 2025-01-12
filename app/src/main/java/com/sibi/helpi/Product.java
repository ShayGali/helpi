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
    private String name;
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

    // Getters and setters:
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", subCategory='" + subCategory + '\'' +
                ", region='" + region + '\'' +
                ", situation='" + ProductCondition + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }


}
