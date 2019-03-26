package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelBookingRepository {
    var mStatus: MutableLiveData<Int>? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mCancelBookingRepository: CancelBookingRepository? = null
        fun getInstance(): CancelBookingRepository {
            if (mCancelBookingRepository == null) {
                mCancelBookingRepository = CancelBookingRepository()
            }
            return mCancelBookingRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun cancelBooking(mContext: Context, mCancel: CancelBooking): LiveData<Int> {
        mStatus = MutableLiveData()
        makeCallToApi(mContext, mCancel)
        return mStatus!!
    }

    /**
     * function will call the api which will return some data and we store the data in MutableLivedata Object
     */
    fun makeCallToApi(mContext: Context, mCancel: CancelBooking) {

        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        var requestCall: Call<ResponseBody> = service.cancelBooking(mCancel)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}
