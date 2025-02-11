package com.sibi.helpi.utils;

public class AppConstants {
    public static final String COLLECTION_PATH = "images";
    public static final String POST_IMAGES_PATH = "post_images";
    public static final String PROFILE_IMAGES_PATH = "profile_images";
    public static final String COLLECTION_POSTS = "posts";
    public static final String COLLECTION_REPORTS = "reports";
    public static final String STORAGE_POSTS = "posts_images";
    public static final String COLLECTION_USERS = "users";
    public static final String IMG_UPLOAD_FAILED = "Image upload failed: ";
    public static final String POST_UPLOAD_FAILED = "Post upload failed: ";

    public static final String REPORT_UPLOAD_FAILED = "Report upload failed: ";

    public enum PostStatus {
        UNDER_REVIEW("Under Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),

        DELETED("Deleted");
        private final String statusText;

        PostStatus(String statusText) {
            this.statusText = statusText;
        }

        public String getStatusText() {
            return statusText;
        }
    }

    public enum UserType {
        DEFAULT_USER, LOCAL_ADMIN, GLOBAL_ADMIN
    }

    public enum ReportStatus {
        PENDING, RESOLVED, REJECTED
    }

    public enum reportReason { //add more reasons
        SPAM("Spam"),
        INAPPROPRIATE_CONTENT("Inappropriate Content"),
        OTHER("Other");

        private final String reasonText;

        reportReason(String reasonText) {
            this.reasonText = reasonText;
        }

        public String getReasonText() {
            return reasonText;
        }

        public static reportReason getReason(String reason) {
           if(reason.equals("Spam")) {
               return SPAM;
           } else if(reason.equals("Inappropriate Content")) {
               return INAPPROPRIATE_CONTENT;
           } else {
               return OTHER;
           }
        }



    }


    public enum PostType {
        PRODUCT, SERVICE,ANY
    }
}
