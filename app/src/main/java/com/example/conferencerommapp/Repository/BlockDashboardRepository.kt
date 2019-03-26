package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockDashboardRepository {
    var mBlockedList: MutableLiveData<List<Blocked>>? = null

    companion object {
        var mBlockedDashboardRepository: BlockDashboardRepository? = null

        fun getInstance(): BlockDashboardRepository{
            if(mBlockedDashboardRepository == null){
                mBlockedDashboardRepository = BlockDashboardRepository()
            }
            return mBlockedDashboardRepository!!
        }
    }

    fun getBlockedList(mContext: Context): LiveData<List<Blocked>>{
        mBlockedList = MutableLiveData()
        makeApiCall(mContext)
        return mBlockedList!!
    }

    fun makeApiCall(mContext: Context) {
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        val blockServices = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Blocked>> = blockServices.getBlockedConference()
        requestCall.enqueue(object: Callback<List<Blocked>>{
            override fun onFailure(call: Call<List<Blocked>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<Blocked>>, response: Response<List<Blocked>>) {
                progressDialog.dismiss()
                if(response.code() == Constants.OK_RESPONSE) {
                    mBlockedList!!.value = response.body()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}