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

    private var mEmployeeRepository: EmployeeRepository? = null
    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessCode =  MutableLiveData<Int>()

    var mErrorCode =  MutableLiveData<Int>()

    var mErrorCodeFromServerForEmployees = MutableLiveData<Int>()

    private var mEmployeeList = MutableLiveData<List<EmployeeList>>()
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

            override fun onFailure(failure: Int) {
                mErrorCode.value = failure
            }

        })
    }

    fun getEmployeeList() {
        mEmployeeRepository = EmployeeRepository.getInstance()
        mEmployeeRepository!!.getEmployeeList(object: ResponseListener {
            override fun onSuccess(success: Any) {
                mEmployeeList.value = success as List<EmployeeList>
            }

            override fun onFailure(failure: Int) {
                mErrorCodeFromServerForEmployees.value = failure
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
    fun returnFailureForBooking(): MutableLiveData<Int> {
        return mErrorCode
    }

    fun returnSuccessForEmployeeList(): MutableLiveData<List<EmployeeList>> {
        return mEmployeeList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailureForEmployeeList(): MutableLiveData<Int> {
        return mErrorCodeFromServerForEmployees
    }
}