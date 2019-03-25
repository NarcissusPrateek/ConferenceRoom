package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerBuildingsRepository {
    var mBuildinglist: MutableLiveData<List<Building>>? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mBuildingsRepository: ManagerBuildingsRepository? = null
        fun getInstance(): ManagerBuildingsRepository {
            if (mBuildingsRepository == null) {
                mBuildingsRepository = ManagerBuildingsRepository()
            }
            return mBuildingsRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getBuildingList(mContext: Context): LiveData<List<Building>> {
        mBuildinglist = MutableLiveData()
        makeApiCall(mContext)
        return mBuildinglist!!
    }

    /**
     * function will call the api which will return some data and we store the data in MutableLivedata Object
     */
    fun makeApiCall(mContext: Context) {

        /**
         * get Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog("Loading...", mContext)
        progressDialog.show()


        /**
         * api call using retorfit
         */
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Building>> = conferenceService.getBuildingList()
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, "Server Not Found!", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                progressDialog.dismiss()
                if (response.code() == 200) {
                    mBuildinglist!!.value = response.body()!!
                } else {
                    Toast.makeText(mContext, "Some Internal Error Occured!", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}
