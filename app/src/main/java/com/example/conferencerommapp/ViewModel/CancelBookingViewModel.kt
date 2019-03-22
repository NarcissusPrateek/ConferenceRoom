package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Repository.CancelBookingRepository

class CancelBookingViewModel: ViewModel() {
    var mCancelBookingRepository: CancelBookingRepository? = null
    var mStatus: MutableLiveData<Int>? = null
    fun cancelBooking(context: Context, mCancel: CancelBooking): LiveData<Int> {
        mCancelBookingRepository = CancelBookingRepository.getInstance()
        mStatus = mCancelBookingRepository!!.cancelBooking(context, mCancel) as MutableLiveData<Int>
        return mStatus!!
    }
}