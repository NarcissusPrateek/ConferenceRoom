package com.example.conferencerommapp.Activity

import android.util.Log
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.conferencerommapp.R
import org.junit.*
import org.junit.runner.RunWith
import java.lang.Exception

@RunWith(AndroidJUnit4::class)
class TestAddingBuilding {

    @get: Rule
    private val mActivityTestRule = ActivityTestRule(AddingBuilding::class.java)
    private val mAddingBuilding = mActivityTestRule.activity!!

    @Before
    fun setUp() {

    }

    @Test
    fun preCondition() {
        try{
            Log.i("--------------", mAddingBuilding.toString())
            Assert.assertNotNull(mAddingBuilding.findViewById(R.id.edit_text_building_name))
        }catch (e: Exception) {
//            Log.i("--------------", e.message)
        }
    }
    @After
    fun tearDown() {

    }
}