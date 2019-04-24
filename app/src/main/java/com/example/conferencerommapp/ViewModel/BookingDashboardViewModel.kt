package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.conferencerommapp.Repository.BookingDashboardRepository
import com.example.conferencerommapp.Repository.UpdateBookingRepository

class BookingDashboardViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBookingDashboardRepository: BookingDashboardRepository? = null

    /**
     * a MutableLivedata variables which will hold the Positive and Negative response from server
     */
    private var mBookingList = MutableLiveData<List<Dashboard>>()

    private var mFailureCodeForBookingList = MutableLiveData<String>()

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccessForCancelBooking = MutableLiveData<Int>()
    var mFailureForCancelBooking = MutableLiveData<String>()


    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun getBookingList(email: String, userId: String, token: String) {
        mBookingDashboardRepository = BookingDashboardRepository.getInstance()
        mBookingDashboardRepository!!.getBookingList(email, userId, token, object : ResponseListener {
            override fun onSuccess(success: Any) {
                mBookingList.value = success as List<Dashboard>
            }

            override fun onFailure(failure: String) {
                mFailureCodeForBookingList.value = failure
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
    fun returnFailure(): MutableLiveData<String> {
        return mFailureCodeForBookingList
    }


//----------------------------------------------------------------------------------------------------------------------

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun cancelBooking(mCancel: CancelBooking, userId: String, token: String) {
        mBookingDashboardRepository = BookingDashboardRepository.getInstance()
        mBookingDashboardRepository!!.cancelBooking(mCancel, userId, token, object : ResponseListener {
            override fun onFailure(failure: String) {
                mFailureForCancelBooking.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccessForCancelBooking.value = success as Int
            }

        })
    }

    /**
     * function will return the MutableLiveData of Int
     */
    fun returnBookingCancelled(): MutableLiveData<Int> {
        return mSuccessForCancelBooking
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnCancelFailed(): MutableLiveData<String> {
        return mFailureForCancelBooking
    }


    //--------------------------------------------------------------------------------------------------------------


}