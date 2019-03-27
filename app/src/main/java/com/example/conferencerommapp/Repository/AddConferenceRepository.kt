package com.example.conferencerommapp.Repository

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddConferenceRepository {

    // mStatus is used to know the Status code from the backend
    var mStatus: MutableLiveData<Int>? = null

    companion object {
        var mAddConferenceRepository: AddConferenceRepository? = null
        fun getInstance():AddConferenceRepository{
            if(mAddConferenceRepository == null){
                mAddConferenceRepository = AddConferenceRepository()
            }
            return mAddConferenceRepository!!
        }
    }


    //Passing the Context and model and call API, In return sends the status of LiveData
    fun addConferenceDetails(mContext:Context,mConferenceRoom : AddConferenceRoom):MutableLiveData<Int>{
        mStatus = MutableLiveData()
        makeAddConferenceRoomApiCall(mContext,mConferenceRoom)
        return mStatus!!
    }

    private fun makeAddConferenceRoomApiCall(mContext: Context, mConferenceRoom: AddConferenceRoom) {

        //ProgreesDialog
        var progressDialog = GetProgress.getProgressDialog("Loading...", mContext)
        progressDialog.show()

        //Retrofit Call
        val addConferenceRoomService: ConferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val addConferenceRequestCall: Call<ResponseBody> = addConferenceRoomService.addConference(mConferenceRoom)

        addConferenceRequestCall.enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                mStatus!!.value = 420
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("@@@@@",response.body().toString())
                //Alert Dialog for Success or Failure of Adding Conference Room
                val addBuildingAlertDialog = AlertDialog.Builder(mContext)
                addBuildingAlertDialog.setTitle("Add Conference")
                progressDialog.dismiss()
                mStatus!!.value = response.code()

                //mStatus return 400 if the Buildings is already present in the Database
                if(mStatus!!.value == 400){
                    addBuildingAlertDialog.setMessage("Conference Room Already Added")
                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = addBuildingAlertDialog.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }

                //mStatus return 500 if the Server Error occurs
                else if (mStatus!!.value == 500){
                    addBuildingAlertDialog.setMessage("Server Error")
                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = addBuildingAlertDialog.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }

                //mStatus return 200 if the Building is Added Succesfully
                else if (mStatus!!.value == 200){
                    addBuildingAlertDialog.setMessage("Room added successfully.")
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