package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
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
    var mConferenceRoomList =  MutableLiveData<List<ConferenceRoom>>()
    var mFailureCode =  MutableLiveData<String>()
    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     */
    fun getConferenceRoomList(mRoom: ManagerConference, userId: String, token: String) {
        mManagerConferenceRoomRepository = ManagerConferenceRoomRepository.getInstance()
        mManagerConferenceRoomRepository!!.getConferenceRoomList(
            mRoom,
            userId,
            token,
            object : ResponseListener {
                override fun onSuccess(success: Any) {
                    mConferenceRoomList.value = success as List<ConferenceRoom>
                }

                override fun onFailure(failure: String) {
                    mFailureCode.value = failure
                }

            }
        )
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccess(): MutableLiveData<List<ConferenceRoom>> {
        return mConferenceRoomList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailure(): MutableLiveData<String> {
        return mFailureCode
    }


}
