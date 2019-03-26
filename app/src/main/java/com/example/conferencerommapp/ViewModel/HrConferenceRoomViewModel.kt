package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Repository.HrConferenceRoomRepository
import com.example.myapplication.Models.ConferenceList

class HrConferenceRoomViewModel : ViewModel() {

    var mHrConferenceRoomRepository: HrConferenceRoomRepository? = null
    var mHrConferenceRoomList: MutableLiveData<List<ConferenceList>>? = null

    fun getConferenceRoomList(mContext: Context, id: Int): MutableLiveData<List<ConferenceList>> {
        if (mHrConferenceRoomList == null) {
            mHrConferenceRoomRepository = HrConferenceRoomRepository.getInstance()
            mHrConferenceRoomList = mHrConferenceRoomRepository!!.getConferenceRoomList(mContext,id) as MutableLiveData<List<ConferenceList>>
        }
        return mHrConferenceRoomList!!
    }

}