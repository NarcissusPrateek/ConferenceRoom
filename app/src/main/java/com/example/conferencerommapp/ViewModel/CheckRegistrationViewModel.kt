package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Repository.CheckRegistrationRepository

class CheckRegistrationViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mCheckRegistrationRepository: CheckRegistrationRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mCode: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun checkRegistration(mContext: Context, email: String): LiveData<Int> {
        mCheckRegistrationRepository = CheckRegistrationRepository.getInstance()
        mCode = mCheckRegistrationRepository!!.checkRegistration(mContext, email) as MutableLiveData<Int>
        return mCode!!
    }
}
