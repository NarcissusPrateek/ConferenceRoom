package com.example.conferencerommapp.Repository

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRepository {
    var mStatus: MutableLiveData<Int>? = null
    companion object {
        var mBookingRepository: BookingRepository? = null
        fun getInstance(): BookingRepository {
            if(mBookingRepository == null) {
                mBookingRepository = BookingRepository()
            }
            return mBookingRepository!!
        }
    }
    fun addBookigDetails(context: Context, mBoooking: Booking): LiveData<Int> {
        if(mStatus == null) {
            mStatus = MutableLiveData()
            makeCallToApi(context, mBoooking)
        }
        return mStatus!!
    }
    fun makeCallToApi(context: Context, mBooking: Booking) {
        var progressDialog = GetProgress.getProgressDialog("Loading...", context)
        progressDialog.show()
        val service = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = service.addBookingDetails(mBooking)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                mStatus!!.value = 404
                Log.i("---on failure on insert", t.message)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("---------", response.body().toString())
                progressDialog.dismiss()
                mStatus!!.value = response.code()
            }
        })
    }
}