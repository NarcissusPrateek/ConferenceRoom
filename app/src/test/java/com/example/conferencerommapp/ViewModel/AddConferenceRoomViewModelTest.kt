package com.example.conferencerommapp.ViewModel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Repository.AddConferenceRepository
import junit.framework.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class AddConferenceRoomViewModelTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var mAddConferenceRoomRepository: AddConferenceRepository
    lateinit var mAddConference: AddConferenceRoom
    lateinit var mAddConferenceEmpty: AddConferenceRoom
    val mStatusFalseValue = MutableLiveData<Int>()
    val mStatus= MutableLiveData<Int>()

    @Before
    fun setUp() {
        mStatus.value = 200
        mStatusFalseValue.value = 555
        mAddConferenceRoomRepository = mock(AddConferenceRepository::class.java)
        mAddConference = AddConferenceRoom(1,"Vayu",4)
        mAddConferenceEmpty = AddConferenceRoom(1,"",4)
        `when`(mAddConferenceRoomRepository.addConferenceDetails(mAddConference)).thenReturn(mStatus)
        `when`(mAddConferenceRoomRepository.addConferenceDetails(mAddConferenceEmpty)).thenReturn(mStatusFalseValue)
    }

    @Test
    fun testAddingBuilding(){
        Assert.assertSame(mStatus, mAddConferenceRoomRepository.addConferenceDetails(mAddConference))
    }

}