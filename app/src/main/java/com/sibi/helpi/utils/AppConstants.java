package com.sibi.helpi.utils;

public class AppConstants {
    public static final String COLLECTION_PATH = "images";
    public static final String POST_IMAGES_PATH = "post_images";
    public static final String PROFILE_IMAGES_PATH = "profile_images";
    public static final String COLLECTION_POSTS = "posts";
    public static final String STORAGE_POSTS = "posts_images";

    public enum PostStatus {
        UNDER_REVIEW("Under Review"),
        APPROVED("Approved"),
        REJECTED("Rejected");
        private final String statusText;
        PostStatus(String statusText) {
            this.statusText = statusText;
        }
        public String getStatusText() {
            return statusText;
        }
    }
}
