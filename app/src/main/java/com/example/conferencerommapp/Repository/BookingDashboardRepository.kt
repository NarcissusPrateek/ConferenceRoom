package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookingDashboardRepository {
    var mBookingList: MutableLiveData<List<Dashboard>>? = null

    companion object {
        var mBookingDashboardRepository: BookingDashboardRepository? = null
        fun getInstance(): BookingDashboardRepository {
            if (mBookingDashboardRepository == null) {
                mBookingDashboardRepository = BookingDashboardRepository()
            }
            return mBookingDashboardRepository!!
        }
    }
    fun getBookingList(context: Context, email: String): LiveData<List<Dashboard>> {
        mBookingList = MutableLiveData()
        makeApiCall(context, email)
        return mBookingList!!
    }
    fun makeApiCall(context: Context, email: String) {
        var progressDialog = GetProgress.getProgressDialog("Loading...", context)
        progressDialog.show()
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Dashboard>> = conferenceService.getDashboard(email!!)
        requestCall.enqueue(object : Callback<List<Dashboard>> {
            override fun onFailure(call: Call<List<Dashboard>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(context, "Server not Found!", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<List<Dashboard>>, response: Response<List<Dashboard>>) {
                progressDialog.dismiss()
                Log.i("------112dashboard list", response.body().toString())
                mBookingList!!.value = response.body()
            }
        })
    }
}