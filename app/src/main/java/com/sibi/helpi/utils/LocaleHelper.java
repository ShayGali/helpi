package com.sibi.helpi.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.view.View;

import androidx.collection.ScatterSet;

import com.sibi.helpi.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    public static String getTranslatedCategory(Context context, String selectedCategory, String type, int direction) {
        // Get resources in English (without affecting UI)
        Context englishContext = context.createConfigurationContext(new Configuration());
        englishContext = setLocale(englishContext, "en");

        // Fetch categories in English and Hebrew
        List<String> english = new ArrayList<>();
        List<String> localized = new ArrayList<>();

        switch (type) {
            case "category":
                english = new ArrayList<>(Arrays.asList(englishContext.getResources().getStringArray(R.array.categories)));
                localized = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.categories)));
                break;
            case "subcategory":
                // Fetch subcategories in English and Hebrew for every subcategory
                english = new ArrayList<>(Arrays.asList(englishContext.getResources().getStringArray(R.array.electronics_subcategories)));
                english.addAll(Arrays.asList(englishContext.getResources().getStringArray(R.array.fashion_subcategories)));
                english.addAll(Arrays.asList(englishContext.getResources().getStringArray(R.array.home_subcategories)));
                english.addAll(Arrays.asList(englishContext.getResources().getStringArray(R.array.toys_subcategories)));
                english.addAll(Arrays.asList(englishContext.getResources().getStringArray(R.array.books_subcategories)));
                english.addAll(Arrays.asList(englishContext.getResources().getStringArray(R.array.other_subcategories)));

                // Now for the localized subcategories
                localized = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.electronics_subcategories)));
                localized.addAll(Arrays.asList(context.getResources().getStringArray(R.array.fashion_subcategories)));
                localized.addAll(Arrays.asList(context.getResources().getStringArray(R.array.home_subcategories)));
                localized.addAll(Arrays.asList(context.getResources().getStringArray(R.array.toys_subcategories)));
                localized.addAll(Arrays.asList(context.getResources().getStringArray(R.array.books_subcategories)));
                localized.addAll(Arrays.asList(context.getResources().getStringArray(R.array.other_subcategories)));
                break;
            case "condition":
                english = new ArrayList<>(Arrays.asList(englishContext.getResources().getStringArray(R.array.product_condition)));
                localized = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.product_condition)));
                break;
            case "reportReason":
                english = new ArrayList<>(Arrays.asList(englishContext.getResources().getStringArray(R.array.report_reasons)));
                localized = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.report_reasons)));
                break;
            default:
                return selectedCategory; // Default to original if type is not recognized
        }

        if (direction == AppConstants.ENGLISH_TO_LOCAL) {
            // Find the index of the selected category in the English list
            for (int i = 0; i < english.size(); i++) {
                if (english.get(i).equals(selectedCategory)) {
                    return localized.get(i); // Return localized version
                }
            }
            return selectedCategory; // Default to original if not found
        } else if (direction == AppConstants.LOCAL_TO_ENGLISH) {

            // Find the index of the selected category in the localized list
            for (int i = 0; i < localized.size(); i++) {
                if (localized.get(i).equals(selectedCategory)) {
                    return english.get(i); // Return English version
                }
            }
            return selectedCategory; // Default to original if not found
        }
        return selectedCategory; // Default to original if direction is not recognized
    }
}