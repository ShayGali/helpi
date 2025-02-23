package com.sibi.helpi.stats;

import com.sibi.helpi.models.User;

public class UserState {
    // Factory methods for different states
    public static UserState loading() {
        return new UserState(true, null, null);
    }

    public static UserState success(User user) {
        return new UserState(false, user, null);
    }

    public static UserState error(String error) {
        return new UserState(false, null, error);
    }

    public static UserState idle() {
        return new UserState(true, null, null);
    }


    private final boolean isLoading;
    private final User user;
    private final String error;

    public UserState(boolean isLoading, User user, String error) {
        this.isLoading = isLoading;
        this.user = user;
        this.error = error;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public User getUser() {
        return user;
    }

    public String getError() {
        return error;
    }

    public boolean isIdle() {
        return !isLoading && user == null && error == null;
    }
}