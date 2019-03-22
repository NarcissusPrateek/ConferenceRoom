package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelBookingRepository {
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        var mCancelBookingRepository: CancelBookingRepository? = null
        fun getInstance(): CancelBookingRepository {
            if (mCancelBookingRepository == null) {
                mCancelBookingRepository = CancelBookingRepository()
            }
            return mCancelBookingRepository!!
        }
    }

    fun cancelBooking(context: Context, mCancel: CancelBooking): LiveData<Int> {
        mStatus = MutableLiveData()
        makeCallToApi(context, mCancel)
        return mStatus!!
    }

    fun makeCallToApi(context: Context, mCancel: CancelBooking) {
        var progressDialog = GetProgress.getProgressDialog("Processing...", context)
        progressDialog.show()
        var serviceBuilder = Servicebuilder.buildService(ConferenceService::class.java)
        var requestCall: Call<ResponseBody> = serviceBuilder.cancelBooking(mCancel)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Error on Failure", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    mStatus!!.value = response.code()
                } else {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}