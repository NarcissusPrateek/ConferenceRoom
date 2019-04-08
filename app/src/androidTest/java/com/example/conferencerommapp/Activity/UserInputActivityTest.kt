package com.example.conferencerommapp.Activity

import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.PickerActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel
import fr.ganfra.materialspinner.MaterialSpinner
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UserInputActivityTest {

    /**
     *  A ActivityTestRule for the Activity UserInputActivity which launch the activty for each test while running
     */
    @get: Rule
    val mActivityTestRule = ActivityTestRule<UserInputActivity>(UserInputActivity::class.java)

    /**
     *  some activity view fields
     */
    private lateinit var dateEditText: EditText
    private lateinit var fromTimeEditText: EditText
    private lateinit var toTimeEditText: EditText
    private lateinit var capacitySpinnerEditText: MaterialSpinner


    /**
     * to check whether the view wit defind ID is present or not
     */
    @Test
    fun getFromTimeEditText() {
        fromTimeEditText = mActivityTestRule.activity.findViewById(R.id.fromTime)
        assertNotNull(fromTimeEditText)
    }

    /**
     * to check whether the view wit defind ID is present or not
     */
    @Test
    fun getToTimeEditText() {
        toTimeEditText = mActivityTestRule.activity.findViewById(R.id.toTime)
        assertNotNull(toTimeEditText)
    }

    /**
     * to check whether the view wit defind ID is present or not
     */
    @Test
    fun getDateEditText() {
        dateEditText = mActivityTestRule.activity.findViewById(R.id.date)
        assertNotNull(dateEditText)

    }

    /**
     * to check whether the view wit defind ID is present or not
     */
    @Test
    fun getCapacitySpinner() {
        capacitySpinnerEditText = mActivityTestRule.activity.findViewById(R.id.spinner2)
        assertNotNull(capacitySpinnerEditText)
    }

    /**
     * set date to DatePickerDialog attached to dateEditText
     */
    @Test
    fun setDateEditText() {
        val year = 2019
        val month = 5
        val day = 7
        onView(withId(R.id.date)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                month,
                day
            )
        )
        onView(withId(android.R.id.button1)).perform(click());
        // onView(withId(R.id.date)).check(matches(withText("2019-05-07")))
    }

    /**
     * set date to TimePickerDialog attached to fromTimeEditText
     */
    @Test
    fun setFromTimeEditText() {
        val hour = 12
        val min = 20
        onView(withId(R.id.fromTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hour,
                min
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
        //onView(withId(R.id.fromTime)).check(matches(withText("12:20")))

    }

    /**
     * set date to TimePickerDialog attached to fromTimeEditText
     */
    @Test
    fun setToTimeEditText() {
        val hour = 13
        val min = 20
        onView(withId(R.id.toTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hour,
                min
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
    }

    @Test
    fun setCapacitySpinner() {
        val spinnerItem = "16"
        /**
         * find the view with the is spinner2 and perform click on the view
         */
        onView(withId(R.id.spinner2)).perform(click())
        /**
         * this statement checks whether after clicking on spinner, spinner is displayed or not
         */
        //onView(withText("Select Capacity")).check(matches(isDisplayed()))
        /**
         *  this statement click an item with data 4
         */
        onView(withText(spinnerItem)).perform(click())
        /**
         * this statement checks after clicking the item the item is displayed in spineer edit text or not
         */
        //onView(withText(spinnerItem)).check(matches(isDisplayed()))


    }

    @Test
    fun validateAllInputFields() {
        val year = 2019
        val month = 5
        val day = 7
        onView(withId(R.id.date)).perform(click())
        onView(withClassName(Matchers.equalTo(DatePicker::class.java.name))).perform(
            PickerActions.setDate(
                year,
                month,
                day
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
        val hour = 12
        val min = 20
        onView(withId(R.id.fromTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hour,
                min
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
        val hour1 = 13
        val min1 = 20
        onView(withId(R.id.toTime)).perform(click())
        onView(withClassName(Matchers.equalTo(TimePicker::class.java.name))).perform(
            PickerActions.setTime(
                hour1,
                min1
            )
        )
        onView(withId(android.R.id.button1)).perform(click())
        val spinnerItem = "16"
        onView(withId(R.id.spinner2)).perform(click())
        onView(withText(spinnerItem)).perform(click())


    }
    @Test
    fun validate() {
        val mAddingBuildingViewModel = AddBuildingViewModel()
        assertEquals(5, mAddingBuildingViewModel.validateTime(5))
    }
}