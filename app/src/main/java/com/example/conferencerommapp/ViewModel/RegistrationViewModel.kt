package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.Repository.RegistrationRepository

class RegistrationViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mRegistrationRepository: RegistrationRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccess = MutableLiveData<Int>()
    var mFailure = MutableLiveData<String>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addEmployee(mEmployee: Employee, userId: String, token: String) {
        mRegistrationRepository = RegistrationRepository.getInstance()
        mRegistrationRepository!!.addEmployee(mEmployee, userId, token, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccess.value = success as Int
            }

            override fun onFailure(failure: String) {
                mFailure.value = failure
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccessForRegistration(): MutableLiveData<Int> {
        return mSuccess
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForRegistration(): MutableLiveData<String> {
        return mFailure
    }
}