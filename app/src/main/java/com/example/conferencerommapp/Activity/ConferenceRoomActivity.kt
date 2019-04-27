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
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel
import es.dmoral.toasty.Toasty

@Suppress("DEPRECATION")
class ConferenceRoomActivity : AppCompatActivity() {

    private lateinit var mConferenceRoomViewModel: ConferenceRoomViewModel
    private lateinit var mCustomAdapter: ConferenceRoomAdapter
    @BindView(R.id.conference_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>")

        mIntentDataFromActivity = getIntentData()
        val mFetchRoom = setDataToObjectForApiCall(mIntentDataFromActivity)
        init()
        observeData()
        getViewModel(mIntentDataFromActivity, mFetchRoom)
    }

    /**
     * get intent data from previous activity
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get the object of ViewModel class and by using this object we call the api and set the observer on the function
     */
    private fun getViewModel(mIntentDataFromActivity: GetIntentDataFromActvity, mFetchRoom: FetchConferenceRoom) {
        progressDialog.show()
        mConferenceRoomViewModel.getConferenceRoomList(mFetchRoom, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * initialize lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mConferenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        //positive response
        mConferenceRoomViewModel.returnSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if(it.isEmpty()) {
                Toasty.info(this,  getString(R.string.room_not_available), Toast.LENGTH_SHORT, true).show()
                finish()
            }else {
                mCustomAdapter = ConferenceRoomAdapter(
                    it!!,
                    object : ConferenceRoomAdapter.BtnClickListener {
                        override fun onBtnClick(roomId: String?, roomname: String?) {
                            mIntentDataFromActivity.roomId = roomId
                            mIntentDataFromActivity.roomName = roomname
                            goToNextActivity(mIntentDataFromActivity)
                        }
                    })
                mRecyclerView.adapter = mCustomAdapter
            }
        })

        // Negative response
        mConferenceRoomViewModel.returnFailure().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
                finish()
            }
        })
    }

    /**
     * intent to the BookingActivity
     */
    fun goToNextActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val intent = Intent(this@ConferenceRoomActivity, SelectMeetingMembersActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
        startActivity(intent)
    }

    /**
     * onRestart of activity function will update the data by calling the api
     * and this updated data will be obsereved by Observer
     */
    override fun onRestart() {
        super.onRestart()
        mConferenceRoomViewModel.getConferenceRoomList(setDataToObjectForApiCall(mIntentDataFromActivity), getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    private fun setDataToObjectForApiCall(mIntentDataFromActivity: GetIntentDataFromActvity): FetchConferenceRoom {
        val mFetchRoom = FetchConferenceRoom()
        mFetchRoom.fromTime = mIntentDataFromActivity.fromTime
        mFetchRoom.toTime = mIntentDataFromActivity.toTime
        mFetchRoom.capacity = mIntentDataFromActivity.capacity!!.toInt()
        mFetchRoom.buildingId = mIntentDataFromActivity.buildingId!!.toInt()
        return mFetchRoom
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
