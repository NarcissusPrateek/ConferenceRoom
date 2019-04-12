package com.example.conferencerommapp.Repository

import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.AddBuilding
import junit.framework.Assert.assertSame
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito.doAnswer
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.invocation.InvocationOnMock
import org.mockito.Mockito.doAnswer



@RunWith(MockitoJUnitRunner::class)
class AddBuildingRepositoryTest {

    lateinit var mAddBuildingRepository: AddBuildingRepository

    var mStatus =  MutableLiveData<Int>()

    private lateinit var mAddBuildingData: AddBuilding

    @Before
    fun setUp() {
        mAddBuildingRepository = AddBuildingRepository()

        mAddBuildingRepository = mock(AddBuildingRepository::class.java)
        mAddBuildingData = AddBuilding("Main building", "Kora")
        `when`(mAddBuildingRepository.makeAddBuildingApiCall(mAddBuildingData)).thenAnswer {
            mAddBuildingRepository.mStatus!!.setValue(Constants.OK_RESPONSE)
        }
    }
    @Test
    fun getInstance_objectOfRepository() {
        assertSame(123, mAddBuildingRepository.addBuildingDetails(mAddBuildingData))
    }
}




//        doAnswer { invocation ->
//            mAddBuildingRepository.mSuccessForBooking!!.value = 123
//            null
//        }.`when`(mAddBuildingRepository).makeAddBuildingApiCall(mAddBuildingData)



//        doAnswer({
//            mSuccessForBooking.value = Constants.OK_RESPONSE
//        } as Answer<*>).`when`(mAddBuildingRepository).makeAddBuildingApiCall(mAddBuildingData)
//
