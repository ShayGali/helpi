package com.sibi.helpi.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.utils.AppConstants.PostStatus;

import java.io.Serializable;
import java.util.List;

public interface Postable extends Serializable {
    String getId();

    void setId(String id);

    String getTitle();

    String getDescription();

    String getCategory();

    String getSubCategory();

    GeoPoint getLocation();

    String getUserId();

    List<String> getImageUrls();

    void setImageUrls(List<String> imageUrls);

    PostStatus getStatus();

    void setStatus(PostStatus status);

    String toString();


}
