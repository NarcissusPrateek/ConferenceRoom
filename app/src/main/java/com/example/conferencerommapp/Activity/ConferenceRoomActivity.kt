package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import kotlinx.android.synthetic.main.activity_conference_room.*
import kotlinx.android.synthetic.main.toolbar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConferenceRoomActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))
        loadConferenceRoom()
    }

    fun loadConferenceRoom() {

        progressDialog = ProgressDialog(this@ConferenceRoomActivity)
        progressDialog!!.setMessage("Loading....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()


        val bundle: Bundle = intent.extras
        val from = bundle.get(UserInputActivity.EXTRA_FROM_TIME).toString()
        val to = bundle.get(UserInputActivity.EXTRA_TO_TIME).toString()
        val date = bundle.get(UserInputActivity.EXTRA_Date).toString()
        val capacity = bundle.get(UserInputActivity.EXTRA_CAPACITY).toString()
        val buildingId = bundle.get(UserInputActivity.EXTRA_BUILDING_ID).toString()
        val building_name = bundle.get(UserInputActivity.EXTRA_BUILDING_NAME).toString()


        var inputs = FetchConferenceRoom()
        inputs.FromTime = from
        inputs.ToTime = to
        inputs.Capacity = capacity.toInt()
        inputs.BId = buildingId.toInt()


        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<ConferenceRoom>> = conferenceService.getConferenceRoomList(inputs)
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
                        val builder = AlertDialog.Builder(this@ConferenceRoomActivity)
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
                                    val intent = Intent(this@ConferenceRoomActivity, BookingActivity::class.java)
                                    intent.putExtra(UserInputActivity.EXTRA_ROOM_ID, roomId)
                                    intent.putExtra(UserInputActivity.EXTRA_BUILDING_ID, buildingId)
                                    intent.putExtra(UserInputActivity.EXTRA_FROM_TIME, from)
                                    intent.putExtra(UserInputActivity.EXTRA_TO_TIME, to)
                                    intent.putExtra(UserInputActivity.EXTRA_Date, date)
                                    intent.putExtra(UserInputActivity.EXTRA_CAPACITY, capacity)
                                    intent.putExtra(UserInputActivity.EXTRA_ROOM_NAME, roomname)
                                    intent.putExtra(UserInputActivity.EXTRA_BUILDING_NAME, building_name)
                                    startActivity(intent)
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
