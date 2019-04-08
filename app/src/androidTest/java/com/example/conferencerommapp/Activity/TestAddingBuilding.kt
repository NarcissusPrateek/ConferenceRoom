package com.example.conferencerommapp.Activity

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.conferencerommapp.R
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestAddingBuilding {

    @get: Rule
    val mActivityTestRule = ActivityTestRule(AddingBuilding::class.java)

    @Before
    fun setUp() {
    }

    /**
     * test for whether the views with id's present or not
     */
    @Test
    fun preCondition() {
        assertNotNull(R.id.edit_text_building_name)
        assertNotNull(R.id.edit_text_building_place)
        assertNotNull(R.id.button_add_building)
    }

    /**
     *  test for edit text with some dummy data
     */
    @Test
    fun checkEditTextContent() {
        onView(withId(R.id.edit_text_building_name))
            .check(matches(isDisplayed()))

        onView(withId(R.id.edit_text_building_place))
            .check(matches(isDisplayed()))
    }
    @Test
    fun addButtonTest() {
        onView(withId(R.id.button_add_building))
            .perform(click())
    }


    @After
    fun tearDown() {
    }
}