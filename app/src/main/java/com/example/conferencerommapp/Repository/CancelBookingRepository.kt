package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.CancelBooking
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancelBookingRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mCancelBookingRepository: CancelBookingRepository? = null
        fun getInstance(): CancelBookingRepository {
            if (mCancelBookingRepository == null) {
                mCancelBookingRepository = CancelBookingRepository()
            }
            return mCancelBookingRepository!!
        }
    }

    /**
     * function will make the API Call and call the interface method with data from server
     */
    fun cancelBooking(mCancel: CancelBooking, listener: ResponseListener) {
        /**
         * api call using retrofit
         */
        val service = Servicebuilder.getObject()
        var requestCall: Call<ResponseBody> = service.cancelBooking(mCancel)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(response.code())
                }
            }
        })
    }
}
