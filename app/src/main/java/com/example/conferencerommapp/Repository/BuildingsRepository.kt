package com.example.conferencerommapp.Repository

import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingsRepository {
    var mBuildinglist: MutableLiveData<List<Building>>? = null

    companion object {
        var mBuildingsRepository: BuildingsRepository? = null
        fun getInstance(): BuildingsRepository {
            if (mBuildingsRepository == null) {
                mBuildingsRepository = BuildingsRepository()
            }
            return mBuildingsRepository!!
        }
    }
    fun getBuildingList(context: Context): LiveData<List<Building>> {
        mBuildinglist = MutableLiveData()
        makeApiCall(context)
        return mBuildinglist!!
    }
    fun makeApiCall(context: Context) {
        var progressDialog = GetProgress.getProgressDialog("Loading...", context)
        progressDialog.show()
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Building>> = conferenceService.getBuildingList()
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                progressDialog.dismiss()
                Log.i("-----------", "error on failure")
            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                progressDialog.dismiss()
                Log.i("-----Building Updated", response.body().toString())
                if (response.isSuccessful) {
                    mBuildinglist!!.value = response.body()!!
                } else {

                }
            }

        })
    }
}