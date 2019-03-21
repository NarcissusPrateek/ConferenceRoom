package com.example.conferencerommapp.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeRepository {
    var mEmployeelist: MutableLiveData<List<EmployeeList>>? = null

    companion object {
        var mEmployeeRepository: EmployeeRepository? = null
        fun getInstance(): EmployeeRepository {
            if (mEmployeeRepository == null) {
                mEmployeeRepository = EmployeeRepository()
            }
            return mEmployeeRepository!!
        }
    }
    fun getEmployeeList(): LiveData<List<EmployeeList>> {
        if (mEmployeelist == null) {
            mEmployeelist = MutableLiveData()
            makeApiCall()
        }
        return mEmployeelist!!
    }
    fun makeApiCall() {
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<EmployeeList>> = conferenceService.getEmployees()
        requestCall.enqueue(object : Callback<List<EmployeeList>> {
            override fun onFailure(call: Call<List<EmployeeList>>, t: Throwable) {

            }
            override fun onResponse(call: Call<List<EmployeeList>>, response: Response<List<EmployeeList>>) {
                if (response.isSuccessful) {
                    mEmployeelist!!.value = response.body()!!
                } else {

                }
            }

        })
    }
}