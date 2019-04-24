package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Dashboard
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDashboardRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mBookingDashboardRepository: BookingDashboardRepository? = null
        fun getInstance(): BookingDashboardRepository {
            if (mBookingDashboardRepository == null) {
                mBookingDashboardRepository = BookingDashboardRepository()
            }
            return mBookingDashboardRepository!!
        }
    }


    /**
     * function will make api call for making a booking
     * and call the interface method with data from server
     */
    fun getBookingList(email: String,userId: String, token: String, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<Dashboard>> = service.getDashboard(token, userId, email)
        requestCall.enqueue(object : Callback<List<Dashboard>> {
            override fun onFailure(call: Call<List<Dashboard>>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<List<Dashboard>>, response: Response<List<Dashboard>>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })

    }

    /**
     * function will make the API Call and call the interface method with data from server
     */
    fun cancelBooking(mCancel: CancelBooking,userId: String, token: String,  listener: ResponseListener) {
        /**
         * api call using retrofit
         */
        val service = ServiceBuilder.getObject()
        var requestCall: Call<ResponseBody> = service.cancelBooking(token, userId, mCancel)
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





