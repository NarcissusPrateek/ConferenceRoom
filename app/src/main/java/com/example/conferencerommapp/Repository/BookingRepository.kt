package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Booking
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingRepository {


    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mBookingRepository: BookingRepository? = null
        fun getInstance(): BookingRepository {
            if (mBookingRepository == null) {
                mBookingRepository = BookingRepository()
            }
            return mBookingRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addBookingDetails(mBooking: Booking, listener: ResponseListener) {
        /**
         * api call using retorfit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.addBookingDetails(mBooking)
        requestCall.enqueue(object : Callback<ResponseBody> {

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }

}
