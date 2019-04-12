package com.example.conferencerommapp.Activity

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel
import junit.framework.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddingBuildingTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mAddingBuilding: AddingBuilding
    private lateinit var mAddBuildingViewModel: AddBuildingViewModel
    private lateinit var mBuildingData: AddBuilding
    private var mStatusOk = MutableLiveData<Int>()
    private var mStatusNotOk = MutableLiveData<Int>()

    /**
     * Initial set up before test run
     */
    @Before
    fun setUp() {
        mAddingBuilding = AddingBuilding()
        mAddBuildingViewModel = Mockito.mock(AddBuildingViewModel::class.java)
        mBuildingData = AddBuilding("Main Building", "Koramangala")
        mStatusOk.value = Constants.OK_RESPONSE
        mStatusNotOk.value = Constants.SOME_EXCEPTION

        /**
         * mock the function addBuildingDetails for the expected data and success response
         */
        Mockito.`when`(mAddBuildingViewModel.addBuildingDetails(mBuildingData)).thenReturn(mStatusOk)
        Mockito.`when`(mAddBuildingViewModel.addBuildingDetails(AddBuilding(null, "Koramangala")))
            .thenReturn(mStatusNotOk)
        Mockito.`when`(mAddBuildingViewModel.addBuildingDetails(AddBuilding("Main Building", null)))
            .thenReturn(mStatusNotOk)
        Mockito.`when`(mAddBuildingViewModel.addBuildingDetails(AddBuilding(null, null))).thenReturn(mStatusNotOk)

    }

    /**
     * positive case
     */
    @Test
    fun addBuilding_addBuildingObject_successful() {
        assertSame(mStatusOk, this.mAddBuildingViewModel.addBuildingDetails(mBuildingData))
    }

    /**
     * Negative case when one of the value is missing inside the object
     */
    @Test
    fun addBuilding_addBuildingObjectWithMissisngPlace_notAccepted() {
        assertSame(mStatusNotOk, this.mAddBuildingViewModel.addBuildingDetails(AddBuilding("Main Building", null)))
    }

    /**
     * Negative case when one of the value is missing inside the object
     */
    @Test
    fun addBuilding_addBuildingObjectWithMissisngBuildingName_notAccepted() {
        assertSame(mStatusNotOk, this.mAddBuildingViewModel.addBuildingDetails(AddBuilding(null, "Koramangala")))
    }

    /**
     * Negative case when both values are missing inside the object
     */
    @Test
    fun addBuilding_addBuildingObjectWithBlankObject_notAccepted() {
        assertSame(mStatusNotOk, this.mAddBuildingViewModel.addBuildingDetails(AddBuilding(null, null)))
    }
}