package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.Repository.ManagerBookingRepository

class ManagerBookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mManagerBookingRepository: ManagerBookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(mContext: Context, mBooking: ManagerBooking): MutableLiveData<Int> {
        mManagerBookingRepository = ManagerBookingRepository.getInstance()
        mStatus = mManagerBookingRepository!!.addBookigDetails(mContext, mBooking) as MutableLiveData<Int>
        return mStatus!!
    }
}