package com.sibi.helpi.models;

import android.os.Parcel;
import android.os.Parcelable;

public class MyLatLng implements Parcelable {
    private double latitude;
    private double longitude;

    // No-argument constructor required for Firestore
    public MyLatLng() {
    }

    public MyLatLng(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected MyLatLng(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public static final Creator<MyLatLng> CREATOR = new Creator<>() {
        @Override
        public MyLatLng createFromParcel(Parcel in) {
            return new MyLatLng(in);
        }

        @Override
        public MyLatLng[] newArray(int size) {
            return new MyLatLng[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}