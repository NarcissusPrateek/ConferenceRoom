package com.example.conferencerommapp.Repository

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConferenceRoomRepository {
    var mConferenceRoomList: MutableLiveData<List<ConferenceRoom>>? = null

    companion object {
        var mConferenceRoomRepository: ConferenceRoomRepository? = null
        fun getInstance(): ConferenceRoomRepository {
            if (mConferenceRoomRepository == null) {
                mConferenceRoomRepository = ConferenceRoomRepository()
            }
            return mConferenceRoomRepository!!
        }
    }
    fun getConferenceRoomList(context: Context, room: FetchConferenceRoom): LiveData<List<ConferenceRoom>> {
        if (mConferenceRoomList == null) {
            mConferenceRoomList = MutableLiveData()
            makeApiCall(context, room)
        }
        return mConferenceRoomList!!
    }
    fun makeApiCall(context: Context, room: FetchConferenceRoom) {
        var progressDialog = GetProgress.getProgressDialog("Loading...", context)
        progressDialog.show()
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<ConferenceRoom>> = conferenceService.getConferenceRoomList(room)
        requestCall.enqueue(object : Callback<List<ConferenceRoom>> {
            override fun onFailure(call: Call<List<ConferenceRoom>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context,"server not found!",Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<List<ConferenceRoom>>, response: Response<List<ConferenceRoom>>) {
                Log.i("--------response", response.toString())
                progressDialog.dismiss()
                if (response.isSuccessful) {
                    mConferenceRoomList!!.value = response.body()
                } else {
                    Toast.makeText(context,"Something went wrong!",Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}