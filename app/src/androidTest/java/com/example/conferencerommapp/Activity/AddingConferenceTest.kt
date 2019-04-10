package com.example.conferencerommapp.Activity

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import com.example.conferencerommapp.R
import org.junit.Rule
import org.junit.Assert.assertNotNull
import org.junit.Test

class AddingConferenceTest {

    @get: Rule
    val mActivityTestRule = ActivityTestRule(AddingConference::class.java)

    @Test
    fun preCondition(){
        assertNotNull(R.id.conference_Name)
        assertNotNull(R.id.conference_Capacity)
        assertNotNull(R.id.add_conference_room)
    }
    /**
     *  test for edit text with some dummy data
     */
    @Test
    fun checkEditTextContent() {
        Espresso.onView(ViewMatchers.withId(R.id.conference_Name))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        Espresso.onView(ViewMatchers.withId(R.id.conference_Capacity))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
    @Test
    fun addButtonTest() {
        Espresso.onView(ViewMatchers.withId(R.id.add_conference_room))
            .perform(ViewActions.click())
    }

}