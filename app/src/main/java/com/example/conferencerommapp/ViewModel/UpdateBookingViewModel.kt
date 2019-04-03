package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.conferencerommapp.Repository.UpdateBookingRepository


class UpdateBookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mUpdateBookingRepository: UpdateBookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun updateBookingDetails(context: Context, mUpdateBooking: UpdateBooking): MutableLiveData<Int> {
        mUpdateBookingRepository = UpdateBookingRepository.getInstance()
        mStatus = mUpdateBookingRepository!!.updateBookingDetails(context, mUpdateBooking) as MutableLiveData<Int>
        return mStatus!!
    }
}