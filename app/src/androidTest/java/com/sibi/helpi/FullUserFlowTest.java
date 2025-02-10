package com.sibi.helpi;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.google.firebase.auth.FirebaseAuth;
import com.sibi.helpi.repositories.UserRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class FullUserFlowTest {

    private static final String TEST_EMAIL = "testuser@example.com";
    private static final String TEST_PASSWORD = "123456";
    private static final String TEST_FIRST_NAME = "Test";
    private static final String TEST_LAST_NAME = "User";

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);


    @Rule
    public GrantPermissionRule fineLocationPermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

    @Rule
    public GrantPermissionRule coarseLocationPermissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    @Before
    public void setUp() {
        CountDownLatch latch = new CountDownLatch(1);
        FirebaseAuth.getInstance().signOut();
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                latch.countDown();
            }
        });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        ActivityScenario.launch(MainActivity.class);
    }


    @After
    public void tearDown() {
        // delete test user
        UserRepository userRepository = new UserRepository();
        userRepository.deleteAccount();
        // Clean up test user
        FirebaseAuth.getInstance().signOut();
    }

    @Test
    public void fullUserFlowTest() throws InterruptedException {

        // 0) Verify initial login screen
        onView(withId(R.id.login_email_input)).check(matches(isDisplayed()));

        // 1) Navigate to registration
        onView(withId(R.id.go_to_reg_button)).perform(click());
        onView(withId(R.id.register_button)).check(matches(isDisplayed()));

        // 2) Fill registration form
        onView(withId(R.id.first_name_input_reg))
                .perform(typeText(TEST_FIRST_NAME), closeSoftKeyboard());
        onView(withId(R.id.last_name_input_reg))
                .perform(typeText(TEST_LAST_NAME), closeSoftKeyboard());
        onView(withId(R.id.email))
                .perform(typeText(TEST_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password_input))
                .perform(typeText(TEST_PASSWORD), closeSoftKeyboard());

        // Submit registration
        onView(withId(R.id.register_button)).perform(click());

        // 3) Verify home fragment
        Thread.sleep(3000); // Wait for registration to complete
        onView(withId(R.id.userNameTextView)).check(matches(isDisplayed()));

        // 4) Location permissions already granted via rules

        // 5) Navigate to profile
        onView(withId(R.id.profile_img)).perform(click());

        // 6) Verify profile fragment
        Thread.sleep(1000);
        onView(withId(R.id.logout_btn)).perform(ViewActions.scrollTo());
        onView(withId(R.id.logout_btn)).check(matches(isDisplayed()));

        // 7) Logout
        onView(withId(R.id.logout_btn)).perform(click());
        Thread.sleep(1000); // Wait for logout

        // 8) Login again
        onView(withId(R.id.login_email_input))
                .perform(typeText(TEST_EMAIL), closeSoftKeyboard());
        onView(withId(R.id.password_input))
                .perform(typeText(TEST_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.login_button)).perform(click());

        // 9) Verify successful login
        Thread.sleep(3000); // Wait for login
        onView(withId(R.id.userNameTextView))
                .check(matches(withText("Hi, " + TEST_FIRST_NAME + " " + TEST_LAST_NAME + "!")));
    }
}