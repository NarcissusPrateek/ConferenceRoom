package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockRoomRepository {
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        private var mBlockRoomRepository: BlockRoomRepository? = null
        fun getInstance(): BlockRoomRepository {
            if (mBlockRoomRepository == null) {
                mBlockRoomRepository = BlockRoomRepository()
            }
            return mBlockRoomRepository!!
        }
    }

    /**
     *  function will make API call
     */
    fun blockRoom(mRoom: BlockRoom, listener: ResponseListener) {

        /**
         * make API call usnig retrofit
         */
        val blockRoomApi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = blockRoomApi.blockconference(mRoom)
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



    /**
     * ---------------------------------------------------------------------------------------------------------------------------
     */

    /**
     * function will initialize the MutableLivedata Object and than make API Call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getRoomList(buildingId: Int, listener: ResponseListener) {

        /**
         *  api call using retrofit
         */
        val requestCall: Call<List<BuildingConference>> = Servicebuilder.getObject().getBuildingsConference(buildingId)
        requestCall.enqueue(object : Callback<List<BuildingConference>> {
            override fun onFailure(call: Call<List<BuildingConference>>, t: Throwable) {
                listener.onFailure("Internal Server Code!")
            }

            override fun onResponse(
                call: Call<List<BuildingConference>>,
                response: Response<List<BuildingConference>>
            ) {
                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }

    /**
     * ---------------------------------------------------------------------------------------------------------------------------
     */

    fun blockingStatus(mRoom: BlockRoom, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        var blockRoomApi = Servicebuilder.getObject()
        val requestCall: Call<BlockingConfirmation> = blockRoomApi.blockConfirmation(mRoom)
        requestCall.enqueue(object : Callback<BlockingConfirmation> {
            override fun onFailure(call: Call<BlockingConfirmation>, t: Throwable) {
                listener.onFailure("Internal Server Code!")
            }

            override fun onResponse(call: Call<BlockingConfirmation>, response: Response<BlockingConfirmation>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    if (response.body() == null) {
                        val blockingConfirmation = BlockingConfirmation()
                        blockingConfirmation.mStatus = 0
                        listener.onSuccess(blockingConfirmation)

                    } else {
                        val blockingConfirmation = response.body()
                        blockingConfirmation!!.mStatus = 1
                        listener.onSuccess(blockingConfirmation)
                    }
                }else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }
}