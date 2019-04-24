package com.example.conferencerommapp.Activity

import android.content.Context
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
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.HrConferenceRoomViewModel
import kotlinx.android.synthetic.main.activity_conference_dash_board.*

@Suppress("DEPRECATION")
class ConferenceDashBoard : AppCompatActivity() {

    @BindView(R.id.conference_list)
    lateinit var recyclerView: RecyclerView
    var buildingId: Int = 0
    private lateinit var mHrConferenceRoomViewModel: HrConferenceRoomViewModel
    private lateinit var conferenceRoomAdapter: ConferenceRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_dash_board)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Conference_Rooms) + "</font>")

        ButterKnife.bind(this)
        buildingId = getIntentData()

        mHrConferenceRoomViewModel = ViewModelProviders.of(this).get(HrConferenceRoomViewModel::class.java)
        getConference(buildingId)
    }

    /**
     * Restart the Activity
     */

    override fun onRestart() {
        super.onRestart()
        val pref = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        val buildingId = pref.getInt(Constants.EXTRA_BUILDING_ID, 0)
        mHrConferenceRoomViewModel.getConferenceRoomList(buildingId, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * onClick on this button goes to AddingConference Activity
     */
    @OnClick(R.id.add_conferenece)
    fun addConfereeRoomFloatingActionButton() {
        goToNextActivity(buildingId)

    }

    /**
     * get the buildingId from the BuildingDashboard Activity
     */

    private fun getIntentData(): Int {
        val bundle: Bundle? = intent.extras!!
        return bundle!!.get(Constants.EXTRA_BUILDING_ID)!!.toString().toInt()
    }

    /**
     * Passing Intent and shared preference
     */

    private fun goToNextActivity(buildingId: Int) {

        val pref = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        val editor = pref.edit()
        editor.putInt(Constants.EXTRA_BUILDING_ID, buildingId)
        editor.apply()

        val intent = Intent(this, AddingConference::class.java)
        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
        startActivity(intent)
    }

    /**
     * function calls the ViewModel of ConferecenceRoom and observe data from the database
     */
    private fun getConference(buildingId: Int) {
        /**
         * getting Progress Dialog
         */
        var progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message),this)
        progressDialog.show()
        mHrConferenceRoomViewModel.getConferenceRoomList(buildingId, getUserIdFromPreference(), getTokenFromPreference())
        mHrConferenceRoomViewModel.returnConferenceRoomList().observe(this, Observer {
            progressDialog.dismiss()
            conferenceRoomAdapter = ConferenceRecyclerAdapter(it!!)
            if (it.isEmpty()) {
                empty_view_blocked1.visibility = View.VISIBLE
                empty_view_blocked1.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                empty_view_blocked1.visibility = View.GONE
                recyclerView.adapter = conferenceRoomAdapter
            }
        })
        mHrConferenceRoomViewModel.returnFailureForConferenceRoom().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
                finish()
            }

        })
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        var dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        var builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        var mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient!!.signOut()
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

