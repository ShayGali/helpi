package com.sibi.helpi.models;

public class Product {
    private String title;
    private String category;
    private String subCategory;
    private String region;
    private String status;
    private int imageResourceId;

    // Constructor, getters, and setters
    public Product(String title, String category, String subCategory, String region, String status, int imageResourceId) {
        this.title = title;
        this.category = category;
        this.subCategory = subCategory;
        this.region = region;
        this.status = status;
        this.imageResourceId = imageResourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public String getRegion() {
        return region;
    }

    public String getStatus() {
        return status;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}