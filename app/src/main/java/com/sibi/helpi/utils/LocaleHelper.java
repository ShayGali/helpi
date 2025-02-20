package com.sibi.helpi.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

public class LocaleHelper {
    public static Context setLocale(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        } else {
            configuration.locale = locale;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            context = context.createConfigurationContext(configuration);
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    /**
     * Get the current locale from the configuration
     */
    public static Locale getCurrentLocale(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return configuration.getLocales().get(0);
        } else {
            return configuration.locale;
        }
    }

    /**
     * Check if the current locale is RTL
     */
    public static boolean isRTL(Context context) {
        Locale locale = getCurrentLocale(context);
        return TextUtils.getLayoutDirectionFromLocale(locale) == View.LAYOUT_DIRECTION_RTL;
    }
}