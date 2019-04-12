package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockConfirmationRepository {
    var mConfirmation: MutableLiveData<BlockingConfirmation>? = null
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        var mBlockConfirmationRepository: BlockConfirmationRepository? = null
        fun getInstance(): BlockConfirmationRepository {
            if (mBlockConfirmationRepository == null) {
                mBlockConfirmationRepository = BlockConfirmationRepository()
            }
            return mBlockConfirmationRepository!!
        }
    }

    fun blockingStatus(mContext: Context, room: BlockRoom): LiveData<BlockingConfirmation> {
        mConfirmation = MutableLiveData()
        mStatus = MutableLiveData()
        makeApiCall(mContext, room)
        return mConfirmation!!
    }

    fun makeApiCall(mContext: Context, room: BlockRoom) {
        var progressDialog =
            GetProgress.getProgressDialog(mContext.getString(R.string.progress_message_processing), mContext)
        var blockRoomApi = Servicebuilder.getObject()
        val requestCall: Call<BlockingConfirmation> = blockRoomApi.blockConfirmation(room)
        requestCall.enqueue(object : Callback<BlockingConfirmation> {
            override fun onFailure(call: Call<BlockingConfirmation>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<BlockingConfirmation>, response: Response<BlockingConfirmation>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    if (response.body() == null) {
                        var blockingConfirmation = BlockingConfirmation()
                        blockingConfirmation.mStatus = 0
                        mConfirmation!!.value = blockingConfirmation

                    } else {
                        var blockingConfirmation = response.body()
                        blockingConfirmation!!.mStatus = 1
                        mConfirmation!!.value = blockingConfirmation

                    }

                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }


}