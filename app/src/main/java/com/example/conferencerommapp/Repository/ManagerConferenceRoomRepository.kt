package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.ManagerConference
import com.example.globofly.services.ServiceBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerConferenceRoomRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mManagerConferenceRoomRepository: ManagerConferenceRoomRepository? = null
        fun getInstance(): ManagerConferenceRoomRepository {
            if (mManagerConferenceRoomRepository == null) {
                mManagerConferenceRoomRepository = ManagerConferenceRoomRepository()
            }
            return mManagerConferenceRoomRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getConferenceRoomList(mRoom: ManagerConference, listener: ResponseListener) {
        /**
         * api call using retrofit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<ConferenceRoom>> = service.getMangerConferenceRoomList(mRoom)
        requestCall.enqueue(object : Callback<List<ConferenceRoom>> {
            override fun onFailure(call: Call<List<ConferenceRoom>>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<List<ConferenceRoom>>, response: Response<List<ConferenceRoom>>) {

                if (response.code() == Constants.OK_RESPONSE) {
                    listener.onSuccess(response.body()!!)
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }
}


