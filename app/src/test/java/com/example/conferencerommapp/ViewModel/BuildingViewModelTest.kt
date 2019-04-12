package com.example.conferencerommapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Repository.BuildingsRepository
import junit.framework.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BuildingViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mBuildingsRepository: BuildingsRepository
    private lateinit var mBuildingViewModel: BuildingViewModel

    private var mBuildingList = MutableLiveData<List<Building>>()
    private val dummyList: MutableList<Building> = mutableListOf(
        Building("1", "Main Building", "Kora"),
        Building("2", "SVC", "Kora"),
        Building("3", "Pasta Street", "Kora")
        )

    /**
     * initial setup before the test run
     */
    @Before
    fun setUp() {
        mBuildingViewModel = BuildingViewModel()
        mBuildingsRepository = Mockito.mock(BuildingsRepository::class.java)
        mBuildingList.value = dummyList

        /**
         * stubbing the method according to the response came from backend
         */
        Mockito.`when`(mBuildingsRepository.getBuildingList()).thenReturn(mBuildingList)
    }
    @Test
    fun getBuildingList_requestBuildingList_listOfBuildings() {
      assertSame(mBuildingList, mBuildingViewModel.getBuildingList())
    }

}