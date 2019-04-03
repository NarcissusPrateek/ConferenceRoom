package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.conferencerommapp.R
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class UpdateBookingRepository{
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        var mUpdateBookingRepository: UpdateBookingRepository? = null
        fun getInstance(): UpdateBookingRepository {
            if (mUpdateBookingRepository == null) {
                mUpdateBookingRepository = UpdateBookingRepository()
            }
            return mUpdateBookingRepository!!
        }
    }

    fun updateBookingDetails(mContext: Context, mUpdateBooking: UpdateBooking): LiveData<Int> {
        mStatus = MutableLiveData()
        makeCallToApi(mContext, mUpdateBooking)
        return mStatus!!
    }

    private fun makeCallToApi(mContext: Context, mUpdateBooking: UpdateBooking) {
        /**
         * getting Progress Dialog
         */
        val progressDialog =
            GetProgress.getProgressDialog(mContext.getString(R.string.progress_message_processing), mContext)
        progressDialog.show()

        val service = Servicebuilder.getObject()
        val requestCall: Call<ResponseBody> = service.update(mUpdateBooking)
        requestCall.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                }
                else {
                    try {
                        val dialog = GetAleretDialog.getDialog(mContext, mContext.getString(R.string.status),
                            JSONObject(response.errorBody()!!.string()).getString("Message")
                        )
                        dialog.setPositiveButton(mContext.getString(R.string.ok)) { _, _ ->
                        }
                        GetAleretDialog.showDialog(dialog)
                    }catch (e: Exception) {
                        Log.i("---",e.message)
                    }
                }
            }
        })

    }


}