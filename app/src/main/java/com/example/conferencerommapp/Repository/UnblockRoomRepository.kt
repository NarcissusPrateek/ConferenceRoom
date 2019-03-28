package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UnblockRoomRepository {
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        var mUnblockRoomRepository: UnblockRoomRepository? = null
        fun getInstance():UnblockRoomRepository{
            if(mUnblockRoomRepository == null){
                mUnblockRoomRepository = UnblockRoomRepository()
            }
            return mUnblockRoomRepository!!
        }
    }

    fun unblockRoom(mContext: Context,room : Unblock):MutableLiveData<Int>{
        mStatus = MutableLiveData()
        makeApiCall(mContext,room)
        return mStatus!!
    }

    fun makeApiCall(mContext: Context, room: Unblock) {

        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        val unBlockApi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall : Call<ResponseBody> = unBlockApi.unBlockingConferenceRoom(room)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_LONG).show()

            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog!!.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }

            }

        })

    }
}