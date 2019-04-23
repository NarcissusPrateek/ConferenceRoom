package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddConferenceRepository {

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
    fun addConferenceDetails(mConferenceRoom : AddConferenceRoom, listener: ResponseListener) {
        //Retrofit Call
        val addConferenceRoomService = ServiceBuilder.getObject()
        val addConferenceRequestCall: Call<ResponseBody> = addConferenceRoomService.addConference(mConferenceRoom)

        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful) {
                    listener.onSuccess(response.code())
                }else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }
}