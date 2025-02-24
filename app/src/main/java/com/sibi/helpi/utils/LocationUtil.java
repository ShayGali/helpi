package com.sibi.helpi.utils;

import static com.sibi.helpi.utils.LocaleHelper.getCurrentLocale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.firebase.firestore.GeoPoint;
import com.sibi.helpi.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationUtil {
    public static String getCityFromLocation(Context context, GeoPoint location) {
        if (location == null) {
            return context.getString(R.string.city_not_found);
        }
        Geocoder geocoder = new Geocoder(context, getCurrentLocale(context));
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

    public static String getLocationName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, getCurrentLocale(context));
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return context.getString(R.string.location_not_found);
    }

    public static String getLocationName(Context context, GeoPoint location) {

        if (location == null) {
            return context.getString(R.string.location_not_found);
        }
        return getLocationName(context, location.getLatitude(), location.getLongitude());
    }

    public static boolean isWithinRadius(GeoPoint center, GeoPoint target, double radiusKm) {
        final double R = 6371; // Earth radius in km
        double lat1 = Math.toRadians(center.getLatitude());
        double lon1 = Math.toRadians(center.getLongitude());
        double lat2 = Math.toRadians(target.getLatitude());
        double lon2 = Math.toRadians(target.getLongitude());

        double dlat = lat2 - lat1;
        double dlon = lon2 - lon1;

        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dlon / 2) * Math.sin(dlon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = R * c; // Distance in km

        return distance <= radiusKm;
    }


}
