package com.example.conferencerommapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Activity.AddingBuilding
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.AddBuilding
import junit.framework.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class AddBuildingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var mAddBuildingViewModel: AddBuildingViewModel
    lateinit var mAddBuilding: AddBuilding
    lateinit var mAddBuildingEmptyBuildingName: AddBuilding
    lateinit var mAddBuildingEmptyPlace: AddBuilding
    private val mStatusEmptyBNameValue = MutableLiveData<Int>()
    private val mStatusEmptyPlaceValue = MutableLiveData<Int>()
    private val mStatus = MutableLiveData<Int>()

    @Before
    fun setUp() {
        mStatus.value = 200
        mStatusEmptyBNameValue.value = null
        mStatusEmptyPlaceValue.value = null
        mAddBuildingViewModel = mock(AddBuildingViewModel::class.java)
        mAddBuilding = AddBuilding("Main Building", "Kormangla")
        mAddBuildingEmptyBuildingName = AddBuilding("", "Kormangla")
        mAddBuildingEmptyPlace = AddBuilding("Main Building", "")
        `when`(mAddBuildingViewModel.addBuildingDetails(mAddBuilding)).thenReturn(mStatus)
        `when`(mAddBuildingViewModel.addBuildingDetails(mAddBuildingEmptyBuildingName)).thenReturn(
            mStatusEmptyBNameValue
        )
        `when`(mAddBuildingViewModel.addBuildingDetails(mAddBuildingEmptyPlace)).thenReturn(mStatusEmptyPlaceValue)

    }


    @Test
    fun testAddingBuilding() {
        assertSame(mStatus, mAddBuildingViewModel.addBuildingDetails(mAddBuilding))
    }

    @Throws(NullPointerException::class)
    @Test
    fun testAddingBuilding_withoutBuildingName() {
        assertSame(mStatusEmptyBNameValue, mAddBuildingViewModel.addBuildingDetails(mAddBuildingEmptyBuildingName))
    }

    @Throws(NullPointerException::class)
    @Test
    fun testAddingBuilding_withoutPlace() {
        assertSame(mStatusEmptyPlaceValue, mAddBuildingViewModel.addBuildingDetails(mAddBuildingEmptyPlace))
    }


}