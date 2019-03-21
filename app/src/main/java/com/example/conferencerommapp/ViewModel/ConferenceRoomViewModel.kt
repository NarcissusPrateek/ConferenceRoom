package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Repository.ConferenceRoomRepository

class ConferenceRoomViewModel : ViewModel() {

    var mConferenceRoomRepository: ConferenceRoomRepository? = null
    var mConferenceRoomList: MutableLiveData<List<ConferenceRoom>>? = null

    fun getConferenceRoomList(context: Context, room: FetchConferenceRoom): LiveData<List<ConferenceRoom>> {
        if (mConferenceRoomList == null) {
            mConferenceRoomRepository = ConferenceRoomRepository.getInstance()
            mConferenceRoomList = mConferenceRoomRepository!!.getConferenceRoomList(
                context,
                room
            ) as MutableLiveData<List<ConferenceRoom>>
        }
        return mConferenceRoomList!!
    }
}