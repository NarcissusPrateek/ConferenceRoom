package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmployeeRepository {
    var mEmployeelist: MutableLiveData<List<EmployeeList>>? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mEmployeeRepository: EmployeeRepository? = null
        fun getInstance(): EmployeeRepository {
            if (mEmployeeRepository == null) {
                mEmployeeRepository = EmployeeRepository()
            }
            return mEmployeeRepository!!
        }
    }
    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun getEmployeeList(mContext: Context): LiveData<List<EmployeeList>> {
        if (mEmployeelist == null) {
            mEmployeelist = MutableLiveData()
            makeApiCall(mContext)
        }
        return mEmployeelist!!
    }

    /**
     * function will call the api which will return some data and we store the data in MutableLivedata Object
     */
    fun makeApiCall(mContext: Context) {
        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message), mContext)
        progressDialog.show()

        /**
         * api call using retorfit
         */
        val service = Servicebuilder.getObject()
        val requestCall: Call<List<EmployeeList>> = service.getEmployees()
        requestCall.enqueue(object : Callback<List<EmployeeList>> {
            override fun onFailure(call: Call<List<EmployeeList>>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<List<EmployeeList>>, response: Response<List<EmployeeList>>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mEmployeelist!!.value = response.body()!!
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }

        })
    }
}

