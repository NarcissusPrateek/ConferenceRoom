package com.example.conferencerommapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Repository.CancelBookingRepository

class CancelBookingViewModel : ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mCancelBookingRepository: CancelBookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mSuccess = MutableLiveData<Int>()
    var mFailure = MutableLiveData<Int>()

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun cancelBooking(mCancel: CancelBooking) {
        mCancelBookingRepository = CancelBookingRepository.getInstance()
        mCancelBookingRepository!!.cancelBooking(mCancel, object : ResponseListener {
            override fun onFailure(failure: Int) {
                mFailure.value = failure
            }

            override fun onSuccess(success: Any) {
                mSuccess.value = success as Int
            }

        })
    }

    /**
     * function will return the MutableLiveData of Int
     */
    fun returnBookingCancelled(): MutableLiveData<Int> {
        return mSuccess
    }

    /**
     * function will return the MutableLiveData of Int if something went wrong at server
     */
    fun returnCancelFailed(): MutableLiveData<Int> {
        return mFailure
    }


}