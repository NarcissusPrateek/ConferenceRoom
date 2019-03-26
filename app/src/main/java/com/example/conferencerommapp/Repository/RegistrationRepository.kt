package com.example.conferencerommapp.Repository

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepository {

    /**
     * mStatus is used to know the Status code from the backend
     */
    var mStatus: MutableLiveData<Int>? = null
    var ok: Int? = null

    companion object {
        var mRegistrationRepository: RegistrationRepository? = null
        fun getInstance(): RegistrationRepository{
            if(mRegistrationRepository == null){
                mRegistrationRepository = RegistrationRepository()
            }
            return mRegistrationRepository!!
        }
    }

    /**
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addEmployee(mContext: Context, mEmployee: Employee) : LiveData<Int> {
        mStatus = MutableLiveData()
        makeCallToApi(mContext,mEmployee)
        return mStatus!!
    }


    /**
     * Retrofit Call
     */
    private fun makeCallToApi(mContext: Context, mEmployee: Employee) {


        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message_processing), mContext)
        progressDialog.show()

        val service = Servicebuilder.getObject()
        val requestCall: Call<ResponseBody> = service.addEmployee(mEmployee)

        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if(response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                }else {
                    Toast.makeText(mContext, mContext.getString(R.string.server_error), Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}