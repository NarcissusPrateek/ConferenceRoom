package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.ManagerConference
import com.example.conferencerommapp.Repository.ManagerConferenceRoomRepository

class ManagerConferenceRoomViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mManagerConferenceRoomRepository: ManagerConferenceRoomRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mConferenceRoomList: MutableLiveData<List<ConferenceRoom>>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getConferenceRoomList(context: Context, room: ManagerConference): MutableLiveData<List<ConferenceRoom>> {
        if (mConferenceRoomList == null) {
            mManagerConferenceRoomRepository = ManagerConferenceRoomRepository.getInstance()
            mConferenceRoomList = mManagerConferenceRoomRepository!!.getConferenceRoomList(
                context,
                room
            ) as MutableLiveData<List<ConferenceRoom>>
        }
        return mConferenceRoomList!!
    }
}
