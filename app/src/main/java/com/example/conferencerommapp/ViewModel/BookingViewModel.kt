package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Repository.BookingRepository

class BookingViewModel: ViewModel() {
    var mBookingRepository: BookingRepository? = null
    var mStatus: MutableLiveData<Int>? = null
    fun addBookingDetails(context: Context, mBooking: Booking): MutableLiveData<Int> {
        mBookingRepository = BookingRepository.getInstance()
        mStatus = mBookingRepository!!.addBookigDetails(context, mBooking) as MutableLiveData<Int>
        return mStatus!!
    }
}