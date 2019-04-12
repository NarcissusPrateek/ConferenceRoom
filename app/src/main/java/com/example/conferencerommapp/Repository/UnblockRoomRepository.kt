package com.example.conferencerommapp.Repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnblockRoomRepository {
    companion object {
        private var mUnblockRoomRepository: UnblockRoomRepository? = null
        fun getInstance(): UnblockRoomRepository {
            if (mUnblockRoomRepository == null) {
                mUnblockRoomRepository = UnblockRoomRepository()
            }
            return mUnblockRoomRepository!!
        }
    }
    fun unblockRoom(mRoom: Unblock) {
        val unBlockApi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = unBlockApi.unBlockingConferenceRoom(mRoom)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {

            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {

                } else {

                }

            }
        })
    }
}