package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingsRepository {
    var mBuildinglist: MutableLiveData<List<Building>>? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mBuildingsRepository: BuildingsRepository? = null
        fun getInstance(): BuildingsRepository {
            if (mBuildingsRepository == null) {
                mBuildingsRepository = BuildingsRepository()
            }
            return mBuildingsRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getBuildingList(): LiveData<List<Building>> {
        mBuildinglist = MutableLiveData()
        makeApiCall()
        return mBuildinglist!!
    }

    /**
     * function will call the api which will return some data and we store the data in MutableLivedata Object
     */
    fun makeApiCall() {
       /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<List<Building>> = service.getBuildingList()
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                 mBuildinglist!!.value = null
            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                if (response.code() == Constants.OK_RESPONSE) {
                    mBuildinglist!!.value = response.body()!!
                }
            }

        })
    }
}

