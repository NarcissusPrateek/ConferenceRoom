package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Repository.ConferenceRoomRepository

class ConferenceRoomViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mConferenceRoomRepository: ConferenceRoomRepository? = null

    /**
     * A MutableLiveData variable which will hold the Value for negative response from repository
     */
    private var errorCodeFromServer =  MutableLiveData<String>()

    /**
     * a MutableLiveData variable which will hold the positive response for repository
     */
    var mConferenceRoomList =  MutableLiveData<List<ConferenceRoom>>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getConferenceRoomList(mRoom: FetchConferenceRoom, userId: String, token: String) {
        if (mConferenceRoomRepository == null) {
            mConferenceRoomRepository = ConferenceRoomRepository.getInstance()
        }
        mConferenceRoomRepository!!.getConferenceRoomList(mRoom, userId, token, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mConferenceRoomList.value = success as List<ConferenceRoom>
            }

            override fun onFailure(failure: String) {
                errorCodeFromServer.value = failure
            }

        })
    }


    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccess(): MutableLiveData<List<ConferenceRoom>> {
        return mConferenceRoomList!!
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailure(): MutableLiveData<String> {
        return errorCodeFromServer
    }

}
