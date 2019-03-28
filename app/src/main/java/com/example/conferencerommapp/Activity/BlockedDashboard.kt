package com.example.conferencerommapp

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.conferencerommapp.Activity.BuildingDashboard
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.ViewModel.BlockedDashboardViewModel
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlockedDashboard : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    lateinit var addConferenceRoom: FloatingActionButton
    lateinit var maintenance: FloatingActionButton
    lateinit var menu : FloatingActionMenu
    lateinit var recyclerView: RecyclerView
    lateinit var blockedAdapter : BlockedDashboardNew
    lateinit var mBlockedDashboardViewModel : BlockedDashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_dashboard)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Blocked_Rooms) + "</font>"))

        addConferenceRoom = findViewById(R.id.add_conference)
        maintenance= findViewById(R.id.maintenance)
        menu = findViewById(R.id.menu)
        recyclerView = findViewById(R.id.block_recyclerView)
        menu.setClosedOnTouchOutside(true);

        mBlockedDashboardViewModel = ViewModelProviders.of(this).get(BlockedDashboardViewModel::class.java)
        maintenance.setOnClickListener {
            val maintenanceintent = Intent(applicationContext, Spinner::class.java)
            startActivity(maintenanceintent)
        }

        addConferenceRoom.setOnClickListener {
            val addConferenceintent = Intent(applicationContext, BuildingDashboard::class.java)
            startActivity(addConferenceintent)
        }
        loadBlocking()

    }
     override fun onBackPressed() {
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        mBlockedDashboardViewModel.mBlockDashboardRepository!!.makeApiCall(this)
    }

    private fun loadBlocking() {
        mBlockedDashboardViewModel.getBlockedList(this).observe(this, Observer {
            if (it.isEmpty()){
                empty_view_blocked.visibility = View.VISIBLE
                empty_view_blocked.setBackgroundColor(Color.parseColor("#FFFFFF"))
            }
            else{
                empty_view_blocked.visibility = View.GONE
            }
            blockedAdapter = BlockedDashboardNew(it,this)
            recyclerView.adapter = blockedAdapter

        })
    }
}
