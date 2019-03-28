package com.example.conferencerommapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.Conference_Room_adapter_new
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Repository.HrConferenceRoomRepository
import com.example.conferencerommapp.ViewModel.HrConferenceRoomViewModel
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import com.example.myapplication.Models.ConferenceList
import com.github.clans.fab.FloatingActionButton
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*

import kotlinx.android.synthetic.main.activity_conference_dash_board.*
import kotlinx.android.synthetic.main.dashboard_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ConferenceDashBoard : AppCompatActivity() {
    lateinit var mHrConferenceRoomViewModel : HrConferenceRoomViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var conferenceRoomAdapter : ConferenceRecyclerAdapter
    lateinit var addConferenceButton : FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_dash_board)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Conference_Rooms) + "</font>"))

        var buildingId = getIntentData()
        addConferenceButton = findViewById(R.id.add_conferenece)
        addConferenceButton.setOnClickListener {
            goToNextActivity(buildingId)
        }
        recyclerView = findViewById(R.id.conference_list)
        mHrConferenceRoomViewModel = ViewModelProviders.of(this).get(HrConferenceRoomViewModel::class.java)
        getConference(buildingId)
    }
    fun getIntentData(): Int {
        val bundle: Bundle = intent.extras
        val buildingId = bundle.get(Constants.EXTRA_BUILDING_ID).toString().toInt()
        return buildingId
    }
    private fun goToNextActivity(buildingId: Int) {

        //shared preference code

        var pref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(Constants.EXTRA_BUILDING_ID, buildingId)
        editor.apply()

        val intent = Intent(this, AddingConference::class.java)
        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
        startActivity(intent)
    }
    override fun onRestart() {
        super.onRestart()
        val pref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var buildingId = pref.getInt(Constants.EXTRA_BUILDING_ID, 0)
        mHrConferenceRoomViewModel.mHrConferenceRoomRepository!!.makeApiCall(this,buildingId)
    }

    private fun getConference(buildingId: Int) {
        mHrConferenceRoomViewModel.getConferenceRoomList(this,buildingId).observe(this, Observer {
            conferenceRoomAdapter = ConferenceRecyclerAdapter(it!!)
            if(it.isEmpty()){
                empty_view_blocked1.visibility = View.VISIBLE
                empty_view_blocked1.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }else{
                empty_view_blocked1.visibility = View.GONE
                recyclerView.adapter = conferenceRoomAdapter
            }
        })
    }
}

