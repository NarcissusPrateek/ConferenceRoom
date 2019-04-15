package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import com.example.myapplication.Models.ConferenceList
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HrConferenceRoomRepository {

    companion object {
        private var mHrConferenceRoomRepository: HrConferenceRoomRepository? = null
        fun getInstance(): HrConferenceRoomRepository {
            if (mHrConferenceRoomRepository == null) {
                mHrConferenceRoomRepository = HrConferenceRoomRepository()
            }
            return mHrConferenceRoomRepository!!
        }
    }

    fun getConferenceRoomList(buildingId: Int, listener: ResponseListener) {
        /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<List<ConferenceList>> = service.conferencelist(buildingId)
        requestCall.enqueue(object : Callback<List<ConferenceList>> {
            override fun onFailure(call: Call<List<ConferenceList>>, t: Throwable) {
                listener.onFailure("Internal Server Code!")
            }

            override fun onResponse(call: Call<List<ConferenceList>>, response: Response<List<ConferenceList>>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }
}