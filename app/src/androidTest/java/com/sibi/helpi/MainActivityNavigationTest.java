package com.sibi.helpi;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.espresso.action.ViewActions;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityNavigationTest {

    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testNavigationToHomeFragment() {
        // Click on the home menu item
        Espresso.onView(ViewMatchers.withId(R.id.homeFragment))
                .perform(ViewActions.click());

        // Check if the home fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.homeFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationToSearchPostableFragment() {
        // Click on the search menu item
        Espresso.onView(ViewMatchers.withId(R.id.searchPostableFragment))
                .perform(ViewActions.click());

        // Check if the search postable fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.searchPostableFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testNavigationToOfferPostFragment() {
        // Click on the offer post menu item
        Espresso.onView(ViewMatchers.withId(R.id.offerPostFragment))
                .perform(ViewActions.click());

        // Check if the offer post fragment is displayed
        Espresso.onView(ViewMatchers.withId(R.id.offerPostFragment))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}