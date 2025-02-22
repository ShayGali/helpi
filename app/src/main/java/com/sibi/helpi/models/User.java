package com.sibi.helpi.models;

import java.util.List;
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

    public User() {
    }

    public User(String email, String firstName, String lastName, String phoneNumber, String profileImgUri) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.profileImgUri = profileImgUri;
        this.userType = UserType.DEFAULT_USER;
    }

    public String getUsername() {
        return firstName + " " + lastName;
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

    public boolean isAdmin() {
        return userType == UserType.LOCAL_ADMIN || userType == UserType.GLOBAL_ADMIN;
    }
    public boolean isLocalAdmin() {
        return userType == UserType.LOCAL_ADMIN;
    }
    public boolean isGlobalAdmin() {
        return userType == UserType.GLOBAL_ADMIN;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
