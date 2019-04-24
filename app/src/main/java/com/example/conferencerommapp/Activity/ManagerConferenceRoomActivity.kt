package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerConference
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.ManagerConferenceRoomViewModel
import es.dmoral.toasty.Toasty

@Suppress("DEPRECATION")
class ManagerConferenceRoomActivity : AppCompatActivity() {


    private lateinit var mManagerConferenceRoomViewModel: ManagerConferenceRoomViewModel
    private lateinit var mCustomAdapter: ConferenceRoomAdapter
    private lateinit var mGetIntentDataFromActivity: GetIntentDataFromActvity
    @BindView(R.id.conference_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__conference__room)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>")
        init()
        loadConferenceRoom()
    }

    private fun loadConferenceRoom() {
        mGetIntentDataFromActivity = getIntentData()
        getViewModel(mGetIntentDataFromActivity)
    }

    /**
     * get intent data from previous activity
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * initialize lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mManagerConferenceRoomViewModel = ViewModelProviders.of(this).get(ManagerConferenceRoomViewModel::class.java)
    }
    /**
     * get the object of ViewModel class and by using this object we call the api and set the observer on the function
     */
    private fun getViewModel(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        /**
         * get progress dialog
         */
        progressDialog.show()
        mManagerConferenceRoomViewModel.getConferenceRoomList(setDataToObjectForApiCall(mGetIntentDataFromActvity), getUserIdFromPreference(), getTokenFromPreference())
        mManagerConferenceRoomViewModel.returnSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if(it.isEmpty()) {
                Toasty.info(this, getString(R.string.room_not_available), Toast.LENGTH_SHORT, true).show()
                finish()
            }else {

                var filteredList = getFilteredList(it)
                mCustomAdapter = ConferenceRoomAdapter(
                    filteredList,
                    object : ConferenceRoomAdapter.BtnClickListener {
                        override fun onBtnClick(roomId: String?, roomname: String?) {
                            mGetIntentDataFromActvity.roomName = roomname
                            mGetIntentDataFromActvity.roomId = roomId
                            goToNextActivity(mGetIntentDataFromActvity)
                        }
                    })
                mRecyclerView.adapter = mCustomAdapter
            }
        })
        mManagerConferenceRoomViewModel.returnFailure().observe(this, Observer {
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
     * filter the list based on objects
     */
    private fun getFilteredList(it: List<ConferenceRoom>?): List<ConferenceRoom> {
        var filteredListOfRooms = ArrayList<ConferenceRoom>()
        for(room in it!!) {
            if(filteredListOfRooms.isEmpty()) {
                filteredListOfRooms.add(room)
            }else {
                if(!filteredListOfRooms.contains(room)) {
                    filteredListOfRooms.add(room)
                }
            }
        }
        return filteredListOfRooms
    }


    /**
     * intent to the ManagerBookingActivity
     */
    fun goToNextActivity(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        val intent =
            Intent(this@ManagerConferenceRoomActivity, ManagerSelectMeetingMembers::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActvity)
        startActivity(intent)
    }

    /**
     * onRestart of activity function will update the data by calling the api
     * and this updated data will be obsereved by Observer
     */
    override fun onRestart() {
        super.onRestart()
        mManagerConferenceRoomViewModel.getConferenceRoomList(setDataToObjectForApiCall(mGetIntentDataFromActivity), getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    private fun setDataToObjectForApiCall(mGetIntentDataFromActvity: GetIntentDataFromActvity): ManagerConference {
        val input = ManagerConference()
        input.fromTime = mGetIntentDataFromActvity.fromTimeList
        input.toTime = mGetIntentDataFromActvity.toTimeList
        input.buildingId = mGetIntentDataFromActvity.buildingId!!.toInt()
        return input
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

