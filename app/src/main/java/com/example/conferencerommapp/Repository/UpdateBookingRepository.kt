package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateBookingRepository{
    companion object {
        var mUpdateBookingRepository: UpdateBookingRepository? = null
        fun getInstance(): UpdateBookingRepository {
            if (mUpdateBookingRepository == null) {
                mUpdateBookingRepository = UpdateBookingRepository()
            }
            return mUpdateBookingRepository!!
        }
    }

    /**
     * funcation will make an API call to make request for the updation of booking
     * and call the interface method with data from server
     */
    fun updateBookingDetails(mUpdateBooking: UpdateBooking, listener: ResponseListener) {
        val service = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = service.update(mUpdateBooking)
        requestCall.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                if (response.isSuccessful) {
                    listener.onSuccess(response.code())
                }
                else {
                    try {
                        listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))

                    }catch (e: Exception) {

                    }
                }
            }
        })

    }
}