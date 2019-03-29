package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Repository.AddConferenceRepository

class AddConferenceRoomViewModel: ViewModel()  {
    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mAddConferenceRepository : AddConferenceRepository? = null
    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addConferenceDetails(mContext: Context,mAddConference: AddConferenceRoom): MutableLiveData<Int>{
        mAddConferenceRepository = AddConferenceRepository.getInstance()
        mStatus = mAddConferenceRepository!!.addConferenceDetails(mContext,mAddConference)
        return mStatus!!
    }

}