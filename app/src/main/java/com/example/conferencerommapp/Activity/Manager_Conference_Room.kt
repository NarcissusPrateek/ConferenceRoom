package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.ManagerConference
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import kotlinx.android.synthetic.main.activity_conference_room.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Manager_Conference_Room : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__conference__room)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))
        loadConferenceRoom()
    }

    fun loadConferenceRoom() {

        progressDialog = ProgressDialog(this@Manager_Conference_Room)
        progressDialog!!.setMessage("Loading....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()


        val bundle: Bundle = intent.extras
        val fromDate = bundle.get(Constants.EXTRA_DATE).toString()
        val toDate = bundle.get(Constants.EXTRA_TO_DATE).toString()
        val from = bundle.getStringArrayList(Constants.EXTRA_FROM_TIME_LIST)
        val to = bundle.getStringArrayList(Constants.EXTRA_TO_TIME_LIST)
        val buildingId = bundle.get(Constants.EXTRA_BUILDING_ID).toString()
        val building_name = bundle.get(Constants.EXTRA_BUILDING_NAME).toString()


        var inputs = ManagerConference()
        inputs.FromTime = from
        inputs.ToTime = to
        inputs.BId = buildingId.toInt()


        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<ConferenceRoom>> = conferenceService.getMangerConferenceRoomList(inputs)
        requestCall.enqueue(object : Callback<List<ConferenceRoom>> {
            override fun onFailure(call: Call<List<ConferenceRoom>>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, "on failure on loading rooms" + t.message, Toast.LENGTH_LONG).show()

            }
            override fun onResponse(call: Call<List<ConferenceRoom>>, response: Response<List<ConferenceRoom>>) {
                progressDialog!!.dismiss()
                if (response.isSuccessful) {
                    var conferenceRoomList = response.body()
                    if (conferenceRoomList!!.size == 0) {
                        val builder = AlertDialog.Builder(this@Manager_Conference_Room)
                        builder.setTitle("Availablity Status")
                        builder.setMessage("No Room is available in this building!!!")
                        builder.setCancelable(false)
                        builder.setPositiveButton("Ok") { dialog, which ->
                            finish()
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    } else {
                        conference_recycler_view.adapter = ConferenceRoomAdapter(conferenceRoomList!!,
                            object : ConferenceRoomAdapter.BtnClickListener {
                                override fun onBtnClick(roomId: String?, roomname: String?) {
                                    val intent =
                                        Intent(this@Manager_Conference_Room, ManagerBookingActivity::class.java)
                                    intent.putExtra(Constants.EXTRA_ROOM_ID, roomId)
                                    intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                                    intent.putExtra(Constants.EXTRA_FROM_TIME_LIST, from)
                                    intent.putExtra(Constants.EXTRA_TO_TIME_LIST, to)
                                    intent.putExtra(Constants.EXTRA_DATE, fromDate)
                                    intent.putExtra(Constants.EXTRA_TO_DATE, toDate)
                                    intent.putExtra(Constants.EXTRA_ROOM_NAME, roomname)
                                    intent.putExtra(Constants.EXTRA_BUILDING_NAME, building_name)
                                    startActivity(intent)
                                    finish()

                                }
                            })
                    }

                } else {
                    Toast.makeText(applicationContext, "Unable to Load Conference Room", Toast.LENGTH_LONG).show()
                }

            }

        })
    }
}
