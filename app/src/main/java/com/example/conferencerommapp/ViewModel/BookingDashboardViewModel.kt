package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Repository.BookingDashboardRepository

class BookingDashboardViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBookingDashboardRepository: BookingDashboardRepository? = null

    /**
     * a MutableLivedata variables which will hold the Positive and Negative response from server
     */
    private var mBookingList = MutableLiveData<List<Dashboard>>()

    private var mFailureCode = MutableLiveData<Int>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getBookingList(email: String) {
        mBookingDashboardRepository = BookingDashboardRepository.getInstance()
        mBookingDashboardRepository!!.getBookingList(email, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mBookingList.value = success as List<Dashboard>
            }

            override fun onFailure(failure: Int) {
                mFailureCode.value = failure
            }

        })
    }

    /**
     * function will return the MutableLiveData of List of dashboard
     */
    fun returnSuccess(): MutableLiveData<List<Dashboard>> {
        return mBookingList
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnFailure(): MutableLiveData<Int> {
        return mFailureCode
    }


}