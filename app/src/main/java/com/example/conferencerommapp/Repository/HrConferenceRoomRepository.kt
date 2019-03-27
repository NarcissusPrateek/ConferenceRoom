package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import com.example.myapplication.Models.ConferenceList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HrConferenceRoomRepository {
    var mHrConferenceRoomList : MutableLiveData<List<ConferenceList>>? = null

    companion object {
        var mHrConferenceRoomRepository : HrConferenceRoomRepository? = null
        fun getInstance(): HrConferenceRoomRepository{
            if (mHrConferenceRoomRepository == null) {
                mHrConferenceRoomRepository = HrConferenceRoomRepository()
            }
            return mHrConferenceRoomRepository!!
        }
    }

    fun getConferenceRoomList(mContext: Context, id: Int): LiveData<List<ConferenceList>> {
        mHrConferenceRoomList = MutableLiveData()
        makeApiCall(mContext, id)
        return mHrConferenceRoomList!!
    }

    fun makeApiCall(mContext: Context, id: Int) {

        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<List<ConferenceList>> = service.conferencelist(id)
        requestCall.enqueue(object : Callback<List<ConferenceList>> {
            override fun onFailure(call: Call<List<ConferenceList>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<ConferenceList>>, response: Response<List<ConferenceList>>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mHrConferenceRoomList!!.value = response.body()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

}