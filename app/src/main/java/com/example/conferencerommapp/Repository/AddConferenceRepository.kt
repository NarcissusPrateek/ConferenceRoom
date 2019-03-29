package com.example.conferencerommapp.Repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import org.json.JSONObject
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
        val progressDialog = GetProgress.getProgressDialog("Loading...", mContext)
        progressDialog.show()

        //Retrofit Call
        val addConferenceRoomService: ConferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val addConferenceRequestCall: Call<ResponseBody> = addConferenceRoomService.addConference(mConferenceRoom)

        addConferenceRequestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog.dismiss()
                Toast.makeText(mContext, mContext.getString(R.string.server_not_found), Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressDialog.dismiss()
                if (response.code() == Constants.OK_RESPONSE) {
                    mStatus!!.value = response.code()
                } else {
                    try{
                        val dialog = GetAleretDialog.getDialog(
                            mContext,
                            "Status",
                            JSONObject(response.errorBody()!!.string()).getString("Message")
                        )
                        dialog.setPositiveButton(mContext.getString(R.string.ok)) { _, _ -> }
                        GetAleretDialog.showDialog(dialog)
                    }catch (e: Exception) {
                        Log.e("", e.message)
                    }
                }

////
////                if(response.code()==400){
//                 val jObjError = JSONObject(response.errorBody()!!.string())
////                    Log.i("@@@@@@",jObjError.getString("Message"))
////                }
////                Log.i("@@@@@@",response.toString())
//                //Alert Dialog for Success or Failure of Adding Conference Room
//                val addBuildingAlertDialog = AlertDialog.Builder(mContext)
//                addBuildingAlertDialog.setTitle("Add Conference")
//                progressDialog.dismiss()
//                mStatus!!.value = response.code()
//                //mStatus return 400 if the Buildings is already present in the Database
//                if(mStatus!!.value == 400){
//                    addBuildingAlertDialog.setMessage(jObjError.getString("Message"))
//                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
//                    }
//                    val dialog: AlertDialog = addBuildingAlertDialog.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    dialog.show()
//                }
//
//                //mStatus return 500 if the Server Error occurs
//                else if (mStatus!!.value == 500){
//                    addBuildingAlertDialog.setMessage("Server Error")
//                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
//                    }
//                    val dialog: AlertDialog = addBuildingAlertDialog.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    dialog.show()
//                }
//
//                //mStatus return 200 if the Building is Added Succesfully
//                else if (mStatus!!.value == 200){
//                    addBuildingAlertDialog.setMessage("Room added successfully.")
//                    addBuildingAlertDialog.setPositiveButton("Ok") { dialog, which ->
//                        (mContext as Activity).finish()
//                    }
//                    val dialog: AlertDialog = addBuildingAlertDialog.create()
//                    dialog.setCanceledOnTouchOutside(false)
//                    dialog.show()
//                }
//            }
//
            }
        })
    }

}