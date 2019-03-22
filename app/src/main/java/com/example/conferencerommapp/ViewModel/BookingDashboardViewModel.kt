package com.example.conferencerommapp.ViewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Repository.BookingDashboardRepository

class BookingDashboardViewModel: ViewModel() {
    var mBookingDashboardRepository: BookingDashboardRepository? = null
    var mBookingList: MutableLiveData<List<Dashboard>>? = null
    fun getBookingList(context: Context, email: String): MutableLiveData<List<Dashboard>> {
        if (mBookingList == null) {
            mBookingDashboardRepository = BookingDashboardRepository.getInstance()
        }
        mBookingList = mBookingDashboardRepository!!.getBookingList(context, email) as MutableLiveData<List<Dashboard>>
        return mBookingList!!
    }
}