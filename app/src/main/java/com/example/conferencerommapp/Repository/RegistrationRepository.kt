package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RegistrationRepository {

    companion object {

        private var mRegistrationRepository: RegistrationRepository? = null
        fun getInstance(): RegistrationRepository {
            if (mRegistrationRepository == null) {
                mRegistrationRepository = RegistrationRepository()
            }
            return mRegistrationRepository!!
        }
    }

    /**
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addEmployee(mEmployee: Employee, listener: ResponseListener) {
        /**
         * Retrofit Call
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<ResponseBody> = service.addEmployee(mEmployee)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Code!")
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