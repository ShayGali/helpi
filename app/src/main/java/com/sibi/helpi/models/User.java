package com.sibi.helpi.models;

import java.util.List;

import com.google.firebase.firestore.Exclude;
import com.sibi.helpi.utils.AppConstants.UserType;


public class User {
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<Postable> posts;
    private String profileImgUri;
    private UserType userType;
    private String fcmToken;
    private boolean notificationEnabled;

    public User() {
    }

    public User(String email, String firstName, String lastName, String phoneNumber, String profileImgUri) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImgUri = profileImgUri;
        this.userType = UserType.DEFAULT_USER;
        this.notificationEnabled = true;
    }


public boolean getNotificationEnabled() {
        return notificationEnabled;
    }
    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Postable> getPosts() {
        return posts;
    }

    public void setPosts(List<Postable> posts) {
        this.posts = posts;
    }

    public String getProfileImgUri() {
        return profileImgUri;
    }

    public void setProfileImgUri(String profileImgUri) {
        this.profileImgUri = profileImgUri;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    @Exclude
    public boolean isAdmin() {
        return userType == UserType.LOCAL_ADMIN || userType == UserType.GLOBAL_ADMIN;
    }

    @Exclude
    public boolean isGlobalAdmin() {
        return userType == UserType.GLOBAL_ADMIN;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    @Exclude
    public String getFullName() {
        return firstName + " " + lastName;
    }
    @Exclude
    public String getUsername() {
        return firstName + " " + lastName;
    }
}
