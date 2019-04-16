package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
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
        val blockServices = ServiceBuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Blocked>> = blockServices.getBlockedConference()
        requestCall.enqueue(object : Callback<List<Blocked>> {
            override fun onFailure(call: Call<List<Blocked>>, t: Throwable) {
                listener.onFailure("Internal Server Code!")
            }
            override fun onResponse(call: Call<List<Blocked>>, response: Response<List<Blocked>>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }

        })
    }

    /**
     * make request to server for unblock room
     */
    fun unblockRoom(mRoom: Unblock, listener: ResponseListener) {
        val unBlockApi = ServiceBuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = unBlockApi.unBlockingConferenceRoom(mRoom)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.code()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }

            }
        })
    }
}