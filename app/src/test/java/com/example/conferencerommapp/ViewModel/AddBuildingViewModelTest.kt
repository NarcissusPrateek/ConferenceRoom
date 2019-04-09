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
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class AddBuildingViewModelTest{

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mAddBuildingViewModel: AddBuildingViewModel
    private val buildingName = "Main Building"
    private val buildingPlace = "Kormangla"
    var mStatus = MutableLiveData<Int>()

    @Before
    fun setUp() {
        val mAddBuilding = AddBuilding()
        mAddBuilding.buildingName = buildingName
        mAddBuilding.place = buildingPlace
        mStatus.value = Constants.OK_RESPONSE
        mAddBuildingViewModel = mock(AddBuildingViewModel::class.java)
        `when`(mAddBuildingViewModel.addBuildingDetails(mAddBuilding)).thenReturn(mStatus)
    }
    @Test
    fun addBuilding_buildingObject_added() {
        val mAddBuilding = AddBuilding()
        mAddBuilding.buildingName = buildingName
        mAddBuilding.place = buildingPlace
        assertSame(mStatus, mAddBuildingViewModel.addBuildingDetails(mAddBuilding))
    }
    @Test
    fun addBuilding_buildingObjectWithNullValues_returnNull() {
        val mAddBuilding = AddBuilding()
        assertSame(null, mAddBuildingViewModel.addBuildingDetails(mAddBuilding))
    }
    @Test
    fun addBuilding_buildingObjectWithOneNullValues_returnNull() {
        val mAddBuilding = AddBuilding()
        mAddBuilding.buildingName = buildingName
        assertSame("ekfhkjhffhkefh", mAddBuildingViewModel.addBuildingDetails(mAddBuilding))
    }
}