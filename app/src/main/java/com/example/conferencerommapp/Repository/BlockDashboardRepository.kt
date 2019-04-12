package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockDashboardRepository {
    companion object {
        var mBlockedDashboardRepository: BlockDashboardRepository? = null

        fun getInstance(): BlockDashboardRepository {
            if (mBlockedDashboardRepository == null) {
                mBlockedDashboardRepository = BlockDashboardRepository()
            }
            return mBlockedDashboardRepository!!
        }
    }

    fun getBlockedList(listener: ResponseListener) {
        val blockServices = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Blocked>> = blockServices.getBlockedConference()
        requestCall.enqueue(object : Callback<List<Blocked>> {
            override fun onFailure(call: Call<List<Blocked>>, t: Throwable) {
                listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<List<Blocked>>, response: Response<List<Blocked>>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }
            }

        })
    }
    fun unblockRoom(mRoom: Unblock, listener: ResponseListener) {
        val unBlockApi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = unBlockApi.unBlockingConferenceRoom(mRoom)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onFailure(Constants.INTERNAL_SERVER_ERROR)
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(response.code())
                }

            }
        })
    }
}