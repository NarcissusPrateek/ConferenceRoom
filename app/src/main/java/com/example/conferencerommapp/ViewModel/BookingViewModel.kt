package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Repository.BookingRepository

class BookingViewModel: ViewModel() {

    /**
     * a object which will hold the reference to the corrosponding repository class
     */
    var mBookingRepository: BookingRepository? = null

    /**
     * a MutableLivedata variable which will hold the Value for the Livedata
     */
    var mStatus: MutableLiveData<Int>? = null

    /**
     * function will initialize the repository object and calls the method of repository which will make the api call
     * and function will return the value for MutableLivedata
     */
    fun addBookingDetails(context: Context, mBooking: Booking): MutableLiveData<Int> {
        mBookingRepository = BookingRepository.getInstance()
        mStatus = mBookingRepository!!.addBookingDetails(context, mBooking) as MutableLiveData<Int>
        return mStatus!!
    }
}