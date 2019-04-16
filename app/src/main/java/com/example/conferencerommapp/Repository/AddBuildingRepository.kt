package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.ResponseListener
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.ServiceBuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddBuildingRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mAddBuildingRepository: AddBuildingRepository? = null
        fun getInstance(): AddBuildingRepository {
            if (mAddBuildingRepository == null) {
                mAddBuildingRepository = AddBuildingRepository()
            }
            return mAddBuildingRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     */
    fun addBuildingDetails(mAddBuilding: AddBuilding, listener: ResponseListener) {
        val addBuildingService: ConferenceService = ServiceBuilder.getObject()
        val addBuildingRequestCall: Call<ResponseBody> = addBuildingService.addBuilding(mAddBuilding)
        addBuildingRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onFailure("Internal Server Error!")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    listener.onSuccess(response.code())
                } else {
                    listener.onFailure(JSONObject(response.errorBody()!!.string()).getString("Message"))
                }
            }
        })
    }
}
