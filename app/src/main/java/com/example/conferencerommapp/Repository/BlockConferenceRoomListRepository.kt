package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockConferenceRoomListRepository {
    var mConferenceRoomList: MutableLiveData<List<BuildingConference>>? = null

    companion object {
        private var mBlockConferenceRoomListRepository: BlockConferenceRoomListRepository? = null
        fun getInstane(): BlockConferenceRoomListRepository {
            if (mBlockConferenceRoomListRepository == null) {
                mBlockConferenceRoomListRepository = BlockConferenceRoomListRepository()
            }
            return mBlockConferenceRoomListRepository!!
        }
    }

    fun getRoomList(mContext: Context, buildingId: Int): LiveData<List<BuildingConference>> {
        mConferenceRoomList = MutableLiveData()
        makeApiCall(mContext, buildingId)
        return mConferenceRoomList!!
    }

    fun makeApiCall(mContext: Context, buildingId: Int) {
        val progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        val requestCall: Call<List<BuildingConference>> = Servicebuilder.getObject().getBuildingsConference(buildingId)
        requestCall.enqueue(object : Callback<List<BuildingConference>> {
            override fun onFailure(call: Call<List<BuildingConference>>, t: Throwable) {
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<BuildingConference>>, response: Response<List<BuildingConference>>
            ) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mConferenceRoomList!!.value = response.body()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}