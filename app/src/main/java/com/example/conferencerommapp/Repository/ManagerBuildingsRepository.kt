package com.example.conferencerommapp.Repository

import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Building
import com.example.globofly.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManagerBuildingsRepository {

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        private var mBuildingsRepository: ManagerBuildingsRepository? = null
        fun getInstance(): ManagerBuildingsRepository {
            if (mBuildingsRepository == null) {
                mBuildingsRepository = ManagerBuildingsRepository()
            }
            return mBuildingsRepository!!
        }
    }

    /**
     * function will call the api which will return some data and we store the data in MutableLivedata Object
     */
    fun getBuildingList(userId: String, token: String) {

        /**
         * api call using retorfit
         */
        val service = ServiceBuilder.getObject()
        val requestCall: Call<List<Building>> = service.getBuildingList(token, userId)
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                if (response.code() == Constants.OK_RESPONSE) {

                } else {

                }
            }

        })
    }
}
