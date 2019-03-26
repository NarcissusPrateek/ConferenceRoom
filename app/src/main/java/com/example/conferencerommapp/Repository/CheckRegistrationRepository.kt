package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CheckRegistrationRepository {

    var mCode: MutableLiveData<Int>? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object{
        var mCheckRegistrationRepository: CheckRegistrationRepository? = null
        fun getInstance(): CheckRegistrationRepository {
            if(mCheckRegistrationRepository == null) {
                mCheckRegistrationRepository = CheckRegistrationRepository()
            }
            return mCheckRegistrationRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     */
    fun checkRegistration(mContext: Context, email: String) : LiveData<Int> {
        mCode = MutableLiveData()
        makeCallToApi(mContext, email)
        return mCode!!
    }

    /**
     * make call to api to get the data from backend
     */
    fun makeCallToApi(mContext: Context, email: String) {

        /**
         * getting Progress Dialog
         */
        var progressDialog =  GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<Int> = service.getRequestCode(email)
        requestCall.enqueue(object : Callback<Int> {
            override fun onFailure(call: Call<Int>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mCode!!.value = response.body()
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }
}
