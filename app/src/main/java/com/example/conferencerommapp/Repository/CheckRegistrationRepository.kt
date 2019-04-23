package com.example.conferencerommapp.Repository

import android.content.Context
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.globofly.services.ServiceBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckRegistrationRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object{
        private var mCheckRegistrationRepository: CheckRegistrationRepository? = null
        fun getInstance(): CheckRegistrationRepository {
            if(mCheckRegistrationRepository == null) {
                mCheckRegistrationRepository = CheckRegistrationRepository()
            }
            return mCheckRegistrationRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     */
    fun checkRegistration(mEmail: String, listener: ResponseListener, mContext: Context)  {
        /**
         * api call using retrofit
         */
        val service = ServiceBuilder.getObjectWithContext(mContext)
        val requestCall: Call<Int> = service.getRequestCode(mEmail)
        requestCall.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }





}
