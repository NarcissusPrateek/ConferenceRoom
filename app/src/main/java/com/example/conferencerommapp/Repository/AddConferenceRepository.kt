package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddConferenceRepository {

    // mStatus is used to know the status code from the backend
    var mStatus: MutableLiveData<Int>? = null
    companion object {
        var mAddConferenceRepository: AddConferenceRepository? = null
        fun getInstance():AddConferenceRepository{
            if(mAddConferenceRepository == null){
                mAddConferenceRepository = AddConferenceRepository()
            }
            return mAddConferenceRepository!!
        }
    }

    //Passing the Context and model and call API, In return sends the status of LiveData
    fun addConferenceDetails(mConferenceRoom : AddConferenceRoom):MutableLiveData<Int>{
        mStatus = MutableLiveData()
        makeAddConferenceRoomApiCall(mConferenceRoom)
        return mStatus!!
    }

    private fun makeAddConferenceRoomApiCall(mConferenceRoom: AddConferenceRoom) {

        //Retrofit Call
        val addConferenceRoomService: ConferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val addConferenceRequestCall: Call<ResponseBody> = addConferenceRoomService.addConference(mConferenceRoom)

        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                mStatus!!.value = Constants.INTERNAL_SERVER_ERROR
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                mStatus!!.value = response.code()
            }
        })
    }

}