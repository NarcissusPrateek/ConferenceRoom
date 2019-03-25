package com.example.conferencerommapp.Repository

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.services.ConferenceService
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
     * Retrofit Call of AddBuilding API
     */
    private fun makeCallToApi(mContext: Context, mEmployee: Employee) {


        var progressDialog = GetProgress.getProgressDialog("Loading...", mContext)
        progressDialog.show()

        val addBuildingService: ConferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val addBuildingrequestCall: Call<ResponseBody> = addBuildingService.addEmployee(mEmployee)

        addBuildingrequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                mStatus!!.value = 420
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
                 * mStatus return 400 if the Employee is already present in the Database
                 */
                if(mStatus!!.value == 400){
                    addBuildingAlertDialog.setMessage("Already Registered")
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
                 * mStatus return 200 if the Employee is Added Succesfully
                 */
            }
        })
    }
}