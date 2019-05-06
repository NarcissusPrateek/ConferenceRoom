package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html.fromHtml
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.R
import com.example.conferencerommapp.R.color.*
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BlockedDashboardViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_blocked_dashboard.*

@SuppressLint("Registered")
@Suppress("DEPRECATION")
class BlockedDashboard : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.block_recyclerView)
    lateinit var recyclerView: RecyclerView
    @BindView(R.id.block_dashboard_refresh_layout)
    lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var blockedAdapter: BlockedDashboardNew
    private lateinit var mBlockedDashboardViewModel: BlockedDashboardViewModel
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_dashboard)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Blocked_Rooms) + "</font>")
        init()
        observeData()
        loadBlocking()
    }
    /**
     * Initialize late init fields
     */
    @SuppressLint("ResourceAsColor")
    fun init() {
        mBlockedDashboardViewModel = ViewModelProviders.of(this).get(BlockedDashboardViewModel::class.java)
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        refreshLayout.setColorSchemeColors(colorPrimary)
        refreshOnPull()
    }

    /**
     * refresh on pull
     */
    private fun refreshOnPull() {
        refreshLayout.setOnRefreshListener {
            mBlockedDashboardViewModel.getBlockedList(getUserIdFromPreference(), getTokenFromPreference())
        }
    }

    /**
     * observing data for BlockDashboardList
     */
    private fun observeData(){
        /**
         * observing data for BlockDashboardList
         */
        mBlockedDashboardViewModel.returnBlockedRoomList().observe(this, Observer {
            refreshLayout.isRefreshing = false
            progressDialog.dismiss()
            if (it.isEmpty()) {
                empty_view_blocked.visibility = View.VISIBLE
                r2_block_dashboard.setBackgroundColor(Color.parseColor("#FFFFFF"))
                //empty_view_blocked.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                empty_view_blocked.visibility = View.GONE
                r2_block_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            }
            blockedAdapter = BlockedDashboardNew(
                it,
                this,
                object: BlockedDashboardNew.UnblockRoomListener {
                    override fun onClickOfUnblock(mRoom: Unblock) {
                        unblockRoom(mRoom)
                    }
                })
            recyclerView.adapter = blockedAdapter

        })
        mBlockedDashboardViewModel.returnFailureCodeFromBlockedApi().observe(this, Observer {
            refreshLayout.isRefreshing = false
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
                finish()
            }
        })
        /**
         * observing data for Unblocking
          */
        mBlockedDashboardViewModel.returnSuccessCodeForUnBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_unblocked), Toast.LENGTH_SHORT, true).show()
            mBlockedDashboardViewModel.getBlockedList(getUserIdFromPreference(), getTokenFromPreference())
        })
        mBlockedDashboardViewModel.returnFailureCodeForUnBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
            }

        })
    }

    @OnClick(R.id.maintenance)
    fun blockConferenceActivity() {
        val maintenanceIntent = Intent(applicationContext, BlockConferenceRoomActivity::class.java)
        startActivity(maintenanceIntent)
    }

    /**
     * Redirects to the UserBookingDashBoardActivity
     */
    override fun onBackPressed() {
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }
    /**
     * function calls the ViewModel of blocking on Restart the Activity
     */
    override fun onRestart() {
        super.onRestart()
        mBlockedDashboardViewModel.getBlockedList(getUserIdFromPreference(), getTokenFromPreference())
    }
    /**
     * function calls the ViewModel of blockedList
     */
    private fun loadBlocking() {
        progressDialog.show()
        mBlockedDashboardViewModel.getBlockedList(getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * function calls the ViewModel of Unblock
     */
    fun unblockRoom(mRoom: Unblock) {
        progressDialog.show()
        mBlockedDashboardViewModel.unBlockRoom(mRoom, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }

    /**
     * get token and userId from local storage
     */
    private fun getTokenFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("Token", "Not Set")!!
    }

    private fun getUserIdFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("UserId", "Not Set")!!
    }
}
