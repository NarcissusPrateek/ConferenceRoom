package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Repository.HrConferenceRoomRepository
import com.example.myapplication.Models.ConferenceList

class HrConferenceRoomViewModel : ViewModel() {

    var mHrConferenceRoomRepository: HrConferenceRoomRepository? = null
    var mHrConferenceRoomList = MutableLiveData<List<ConferenceList>>()
    var mFailureCodeForHrConferenceRoom = MutableLiveData<String>()

    fun getConferenceRoomList(buildingId: Int) {
        mHrConferenceRoomRepository = HrConferenceRoomRepository.getInstance()
        mHrConferenceRoomRepository!!.getConferenceRoomList(buildingId, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mHrConferenceRoomList.value = success as List<ConferenceList>
            }

            override fun onFailure(failure: String) {
                mFailureCodeForHrConferenceRoom.value = failure
            }

        })
    }

    fun returnConferenceRoomList(): MutableLiveData<List<ConferenceList>> {
        return mHrConferenceRoomList
    }

    fun returnFailureForConferenceRoom(): MutableLiveData<String> {
        return mFailureCodeForHrConferenceRoom
    }

}
