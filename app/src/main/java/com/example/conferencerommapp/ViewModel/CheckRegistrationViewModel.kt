package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Repository.CheckRegistrationRepository

class CheckRegistrationViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mCheckRegistrationRepository: CheckRegistrationRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<Int>()
    var mFailureCode =  MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun checkRegistration(mEmail: String, userId: String, token: String){
        mCheckRegistrationRepository = CheckRegistrationRepository.getInstance()
        mCheckRegistrationRepository!!.checkRegistration(mEmail, userId, token, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCode.value = success as Int
            }

            override fun onFailure(failure: String) {
                mFailureCode.value = failure
            }

        })
    }

    fun returnSuccessCode(): MutableLiveData<Int> {
        return mSuccessCode
    }
    fun returnFailureCode(): MutableLiveData<String> {
        return mFailureCode
    }
}
