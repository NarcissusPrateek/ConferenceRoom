package com.example.conferencerommapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AddBuildingRepository {

    /**
     * mStatus is used to know the status code from the backend
     */
    var mStatus: MutableLiveData<Int>? = null
    var ok: Int? = null

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
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addBuildingDetails(mAddBuilding: AddBuilding): LiveData<Int> {
        if (mStatus == null) {
            mStatus = MutableLiveData()
        }
        makeAddBuildingApiCall(mAddBuilding)
        return mStatus!!
    }
    /**
     * make call to api to get the data from backend
     */
    private fun makeAddBuildingApiCall(mAddBuilding: AddBuilding) {
        val addBuildingService: ConferenceService = Servicebuilder.getObject()
        val addBuildingRequestCall: Call<ResponseBody> = addBuildingService.addBuilding(mAddBuilding)
        addBuildingRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                mStatus!!.value = Constants.INTERNAL_SERVER_ERROR
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                mStatus!!.value = response.code()
            }
        })
    }
}