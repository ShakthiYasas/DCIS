package com.example.dcis2

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Rule
import org.junit.Test
import com.example.dcis2.DisplayQRDataActivity
import org.hamcrest.CoreMatchers.not

class DisplayQRDataActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(DisplayQRDataActivity::class.java)

    @Test
    fun NextButtonIsDisabledWhenLocationServiceIsDenied() {
        // Simulate location permission being denied
        // Here you might need to mock or simulate permission behavior in the app

        onView(withId(R.id.bSuttonGoButton)) // Replace with your button ID
            .check(matches(not(isEnabled())))
    }

    @Test
    fun NextButtonIsEnabledWhenLocationServiceIsGranted() {
        // Simulate location permission being granted
        // Here you might need to mock or simulate permission behavior in the app

        onView(withId(R.id.bSuttonGoButton)) // Replace with your button ID
            .check(matches(isEnabled()))
    }
}
