package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class RegistrationRepository {

    /**
     * mStatus is used to know the status code from the backend
     */
    var mStatus: MutableLiveData<Int>? = null
    var ok: Int? = null

    companion object {
        private val TAG = RegistrationRepository::class.simpleName
        private var mRegistrationRepository: RegistrationRepository? = null
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


        Log.i("----------",mEmployee.toString())
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
                Log.i("----------",mEmployee.toString())
                progressDialog.dismiss()
                if(response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                }else {
                    try {
                        val dialog = GetAleretDialog.getDialog(mContext, mContext.getString(R.string.status), "${JSONObject(response.errorBody()!!.string()).getString("Message")}")
                        dialog.setPositiveButton(mContext.getString(R.string.ok)) { _, _ ->
                        }
                        GetAleretDialog.showDialog(dialog)
                    }catch (e: Exception) {
                        Log.e(TAG,e.message)
                    }
                }
            }
        })
    }
}