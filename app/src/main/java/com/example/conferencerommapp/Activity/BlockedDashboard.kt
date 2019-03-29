package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html.fromHtml
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.BlockedDashboardNew
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BlockedDashboardViewModel
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*

@SuppressLint("Registered")
@Suppress("DEPRECATION")
class BlockedDashboard : AppCompatActivity() {

    @BindView(R.id.block_recyclerView)
    lateinit var recyclerView: RecyclerView
    private lateinit var blockedAdapter: BlockedDashboardNew
    lateinit var mBlockedDashboardViewModel: BlockedDashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_dashboard)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Blocked_Rooms) + "</font>")

        ButterKnife.bind(this)
        mBlockedDashboardViewModel = ViewModelProviders.of(this).get(BlockedDashboardViewModel::class.java)
        loadBlocking()

    }

    @OnClick(R.id.menu)
    fun floatingActionMenuOptions() {
        menu.setClosedOnTouchOutside(true)
    }

    @OnClick(R.id.maintenance)
    fun blockConferenceActivity() {
        val maintenanceintent = Intent(applicationContext, BlockConferenceRoomActivity::class.java)
        startActivity(maintenanceintent)
    }

    @OnClick(R.id.add_conference)
    fun addConferenceBuildingActivity() {
        val addConferenceintent = Intent(applicationContext, BuildingDashboard::class.java)
        startActivity(addConferenceintent)

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
            if (it.isEmpty()) {
                empty_view_blocked.visibility = View.VISIBLE
                empty_view_blocked.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                empty_view_blocked.visibility = View.GONE
            }
            blockedAdapter = BlockedDashboardNew(it, this)
            recyclerView.adapter = blockedAdapter

        })
    }
}
