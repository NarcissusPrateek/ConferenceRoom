package com.example.conferencerommapp.Repository

import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.ServiceBuilder
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
    fun blockRoom(mRoom: BlockRoom,userId: String, token: String, listener: ResponseListener) {

        /**
         * make API call usnig retrofit
         */
        val blockRoomApi = ServiceBuilder.getObject()
        val requestCall: Call<ResponseBody> = blockRoomApi.blockconference(token, userId, mRoom)
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
    fun getRoomList(buildingId: Int,userId: String, token: String, listener: ResponseListener) {

        /**
         *  api call using retrofit
         */
        val requestCall: Call<List<BuildingConference>> = ServiceBuilder.getObject().getBuildingsConference(token, userId, buildingId)
        requestCall.enqueue(object : Callback<List<BuildingConference>> {
            override fun onFailure(call: Call<List<BuildingConference>>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
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

    fun blockingStatus(mRoom: BlockRoom,userId: String, token: String, listener: ResponseListener) {
        /**
         * API call using retrofit
         */
        var blockRoomApi = ServiceBuilder.getObject()
        val requestCall: Call<BlockingConfirmation> = blockRoomApi.blockConfirmation(token, userId, mRoom)
        requestCall.enqueue(object : Callback<BlockingConfirmation> {
            override fun onFailure(call: Call<BlockingConfirmation>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
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