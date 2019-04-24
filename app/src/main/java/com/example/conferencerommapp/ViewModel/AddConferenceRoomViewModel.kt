package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Repository.AddConferenceRepository

class AddConferenceRoomViewModel : ViewModel() {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mAddConferenceRepository: AddConferenceRepository? = null
    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessForAddingRoom = MutableLiveData<Int>()
    var mFailureForAddingRoom = MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addConferenceDetails(mAddConference: AddConferenceRoom, userId: String, token: String) {
        mAddConferenceRepository = AddConferenceRepository.getInstance()
        mAddConferenceRepository!!.addConferenceDetails(mAddConference, userId, token, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessForAddingRoom.value = success as Int
            }

            override fun onFailure(failure: String) {
                mFailureForAddingRoom.value = failure
            }
        })
    }

    fun returnSuccessForAddingRoom(): MutableLiveData<Int> {
        return mSuccessForAddingRoom
    }
    fun returnFailureForAddingRoom(): MutableLiveData<String> {
        return mFailureForAddingRoom
    }

}