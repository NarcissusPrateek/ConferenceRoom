package com.example.conferencerommapp.Repository

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Activity.AddingBuilding
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.core.content.ContextCompat.startActivity
import android.content.Intent
import android.widget.Toast
import com.example.conferencerommapp.Activity.BuildingDashboard
import com.example.conferencerommapp.R


class AddBuildingRepository {

    /**
     * mStatus is used to know the Status code from the backend
     */
    var mStatus:MutableLiveData<Int>? = null
    var ok: Int? = null

    /**
     * this block provides a static method which will return the object of repository
     * if the object is already their than it return the same
     * or else it will return a new object
     */
    companion object {
        var mAddBuildingRepository:AddBuildingRepository? = null
        fun getInstance():AddBuildingRepository{
            if(mAddBuildingRepository == null){
                mAddBuildingRepository = AddBuildingRepository()
            }
            return mAddBuildingRepository!!
        }
    }

    /**
     * function will initialize the MutableLivedata Object and than call a function for api call
     * Passing the Context and model and call API, In return sends the status of LiveData
     */
    fun addBuildingDetails(mContext: Context,mAddBuilding: AddBuilding) : LiveData<Int>{
        mStatus = MutableLiveData()
        makeAddBuildingApiCall(mContext,mAddBuilding)
        return mStatus!!
    }


    /**
     * make call to api to get the data from backend
     */
    private fun makeAddBuildingApiCall(mContext: Context, mAddBuilding: AddBuilding) {

        /**
         * ProgreesDialog
         */
        var progressDialog = GetProgress.getProgressDialog(mContext.getString(R.string.progress_message_processing), mContext)
        progressDialog.show()

        /**
         * Retrofit Call
         */
        val addBuildingService:ConferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val addBuildingrequestCall:Call<ResponseBody> = addBuildingService.addBuilding(mAddBuilding)

        addBuildingrequestCall.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                /**
                 * Alert Dialog for Success or Failure of Adding Buildings
                 */
                val addBuildingAlertDialog = AlertDialog.Builder(mContext)
                addBuildingAlertDialog.setTitle("Added Building")
                progressDialog.dismiss()
                mStatus!!.value = response.code()
                /**
                 * mStatus return 400 if the Buildings is already present in the Database
                 */
                if(mStatus!!.value == 400){
                    addBuildingAlertDialog.setMessage("Building Already Added")
                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = addBuildingAlertDialog.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }

                /**
                 * mStatus return 500 if the Server Error occurs
                 */
                else if (mStatus!!.value == 500){
                    addBuildingAlertDialog.setMessage("Server Error")
                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = addBuildingAlertDialog.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }

                /**
                 * mStatus return 200 if the Building is Added Succesfully
                 */
                else if (mStatus!!.value == 200){
                    addBuildingAlertDialog.setMessage("Building added successfully.")
                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
                        (mContext as Activity).finish()
                    }
                    val dialog: AlertDialog = addBuildingAlertDialog.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }

        })
    }
}