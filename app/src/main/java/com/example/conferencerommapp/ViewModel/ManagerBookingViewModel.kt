package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.Repository.EmployeeRepository
import com.example.conferencerommapp.Repository.ManagerBookingRepository

class ManagerBookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    private var mManagerBookingRepository: ManagerBookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<Int>()

    var mErrorCode =  MutableLiveData<String>()
   /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(mBooking: ManagerBooking) {
        mManagerBookingRepository = ManagerBookingRepository.getInstance()
        mManagerBookingRepository!!.addBookingDetails(mBooking, object: ResponseListener {
            override fun onSuccess(success: Any) {
                mSuccessCode.value = success as Int
            }

            override fun onFailure(failure: String) {
                mErrorCode.value = failure
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of buildings
     */
    fun returnSuccessForBooking(): MutableLiveData<Int> {
        return mSuccessCode
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForBooking(): MutableLiveData<String> {
        return mErrorCode
    }
}