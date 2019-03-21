package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import kotlinx.android.synthetic.main.activity_conference_room.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConferenceRoomActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    lateinit var mConfereenceRoomViewModel: ConferenceRoomViewModel
    lateinit var customAdapter: ConferenceRoomAdapter
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))


        val bundle: Bundle = intent.extras
        val from = bundle.get(Constants.EXTRA_FROM_TIME).toString()
        val to = bundle.get(Constants.EXTRA_TO_TIME).toString()
        val date = bundle.get(Constants.EXTRA_DATE).toString()
        val capacity = bundle.get(Constants.EXTRA_CAPACITY).toString()
        val buildingId = bundle.get(Constants.EXTRA_BUILDING_ID).toString()
        val building_name = bundle.get(Constants.EXTRA_BUILDING_NAME).toString()

        var mFetchRoom = FetchConferenceRoom()
        mFetchRoom.FromTime = from
        mFetchRoom.ToTime = to
        mFetchRoom.Capacity = capacity.toInt()
        mFetchRoom.BId = buildingId.toInt()

        recyclerView = findViewById(R.id.conference_recycler_view)
        mConfereenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mConfereenceRoomViewModel.getConferenceRoomList(this, mFetchRoom).observe(this, Observer {
            customAdapter = ConferenceRoomAdapter(
                it!!,
                object: ConferenceRoomAdapter.BtnClickListener {
                    override fun onBtnClick(roomId: String?, roomname: String?) {
                        val intent = Intent(this@ConferenceRoomActivity, BookingActivity::class.java)
                        intent.putExtra(Constants.EXTRA_ROOM_ID, roomId)
                        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                        intent.putExtra(Constants.EXTRA_FROM_TIME, from)
                        intent.putExtra(Constants.EXTRA_TO_TIME, to)
                        intent.putExtra(Constants.EXTRA_DATE, date)
                        intent.putExtra(Constants.EXTRA_CAPACITY, capacity)
                        intent.putExtra(Constants.EXTRA_ROOM_NAME, roomname)
                        intent.putExtra(Constants.EXTRA_BUILDING_NAME, building_name)
                        startActivity(intent)
                    }
                })
            recyclerView.adapter = customAdapter
        })
    }
}
