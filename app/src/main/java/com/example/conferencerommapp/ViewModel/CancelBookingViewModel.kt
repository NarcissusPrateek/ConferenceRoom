package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun cancelBooking(context: Context, mCancel: CancelBooking): LiveData<Int> {
        mCancelBookingRepository = CancelBookingRepository.getInstance()
        mStatus = mCancelBookingRepository!!.cancelBooking(context, mCancel) as MutableLiveData<Int>
        return mStatus!!
    }
}