package com.sibi.helpi.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.sibi.helpi.R;
import com.sibi.helpi.models.MyLatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    public static String getCityFromLocation(Context context, MyLatLng location) {
        if(location == null) {
            return context.getString(R.string.city_not_found);
        }
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getString(R.string.city_not_found);
    }

    public static String getLocationNameFromLocation(Context context, MyLatLng location) {

        if (location == null)
        {
            return context.getString(R.string.location_not_found);
        }

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getString(R.string.location_not_found);
    }
}
