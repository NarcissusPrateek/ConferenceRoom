package com.example.conferencerommapp.Repository

import android.content.Context
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

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mBookingDashboardRepository: BookingDashboardRepository? = null
        fun getInstance(): BookingDashboardRepository {
            if (mBookingDashboardRepository == null) {
                mBookingDashboardRepository = BookingDashboardRepository()
            }
            return mBookingDashboardRepository!!
        }
    }


    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getBookingList(mContext: Context, email: String): LiveData<List<Dashboard>> {
        mBookingList = MutableLiveData()
        makeApiCall(mContext, email)
        return mBookingList!!
    }


    fun makeApiCall(mContext: Context, email: String) {

        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog("Loading...", mContext)
        progressDialog.show()

        /**
         * api call using retorfit
         */
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Dashboard>> = conferenceService.getDashboard(email!!)
        requestCall.enqueue(object : Callback<List<Dashboard>> {
            override fun onFailure(call: Call<List<Dashboard>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, "Server not Found! Please restart the application.", Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<List<Dashboard>>, response: Response<List<Dashboard>>) {
                progressDialog.dismiss()
                if(response.code() == 200) {
                    mBookingList!!.value = response.body()
                } else {
                    Toast.makeText(mContext, "Some Internal Server Error Occured!", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}




