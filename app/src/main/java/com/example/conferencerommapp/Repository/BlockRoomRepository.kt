package com.example.conferencerommapp.Repository

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
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
        val blockroomapi = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = blockroomapi.blockconference(room)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                val builder = AlertDialog.Builder(mContext)
                builder.setTitle("Blocking status")
                when {
                    response.code() == Constants.OK_RESPONSE -> mStatus!!.value = response.code()
                    response.code().equals(400) -> {
                        builder.setMessage("Room already blocked!")
                        builder.setPositiveButton("Ok") { _, _ ->
                            (mContext as Activity).finish()
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                    else -> {
                        builder.setMessage("Something went wrong Room can't be Blocked.")
                        builder.setPositiveButton(mContext.getString(R.string.ok)) { _, _ ->
                            (mContext as Activity).finish()
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }
            }

        })
    }
}