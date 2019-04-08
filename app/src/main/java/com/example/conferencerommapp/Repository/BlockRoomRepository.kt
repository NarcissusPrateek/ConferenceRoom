package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.BlockRoom
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
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun blockRoom(mContext: Context, room: BlockRoom): LiveData<Int> {
        mStatus = MutableLiveData()
        makeCallToApi(mContext, room)
        return mStatus!!
    }

    fun makeCallToApi(mContext: Context, room: BlockRoom) {

        /**
         * get progress dialog
         */
        val progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.status), mContext)
        progressDialog.show()
        val blockRoomApi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = blockRoomApi.blockconference(room)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                } else {
                    var mBuilder = GetAleretDialog.getDialog(
                        mContext,
                        mContext.getString(R.string.status),
                        "${JSONObject(response.errorBody()!!.string()).getString("Message")} "
                    )
                    GetAleretDialog.showDialog(mBuilder)
                }
            }

        })
    }
}