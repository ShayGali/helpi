package com.sibi.helpi.models;

import com.sibi.helpi.utils.AppConstants.PostStatus;

import java.util.List;

public interface Postable {
    String getId();
    void setId(String id);
    String getDescription();
    String getCategory();
    String getSubCategory();
    String getRegion();
    String getUserId();
    List<String> getImageUrls();
    void setImageUrls(List<String> imageUrls);
    PostStatus getStatus();
    void setStatus(PostStatus status);
    String toString();
}
