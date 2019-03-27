package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel

class ConferenceRoomActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    lateinit var mConfereenceRoomViewModel: ConferenceRoomViewModel
    lateinit var mCustomAdapter: ConferenceRoomAdapter
    lateinit var mRecyclerView: RecyclerView
    lateinit var mIntentDataFromActvity: GetIntentDataFromActvity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))

        mIntentDataFromActvity = getIntentData()

        var mFetchRoom = setDataToObjectForApiCall(mIntentDataFromActvity)

        getViewModel(mIntentDataFromActvity, mFetchRoom)
    }

    /**
     * get intent data from previous activity
     */
    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get the object of ViewModel class and by using this object we call the api and set the observer on the function
     */
    fun getViewModel(mIntentDataFromActvity: GetIntentDataFromActvity, mFetchRoom: FetchConferenceRoom) {
        mRecyclerView = findViewById(R.id.conference_recycler_view)
        mConfereenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mConfereenceRoomViewModel.getConferenceRoomList(this, mFetchRoom).observe(this, Observer {
            if(it.isEmpty()) {
                showDialog()
            }else {
                mCustomAdapter = ConferenceRoomAdapter(
                    it!!,
                    object : ConferenceRoomAdapter.BtnClickListener {
                        override fun onBtnClick(roomId: String?, roomName: String?) {
                            mIntentDataFromActvity.roomId = roomId
                            mIntentDataFromActvity.roomName = roomName
                            goToNextActivity(mIntentDataFromActvity)
                        }
                    })
                mRecyclerView.adapter = mCustomAdapter
            }

        })
    }

    /**
     * intent to the BookingActivity
     */
    fun goToNextActivity(mIntentDataFromActvity: GetIntentDataFromActvity) {
        val intent = Intent(this@ConferenceRoomActivity, BookingActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActvity)
        startActivity(intent)
    }

    /**
     * onRestart of activity function will update the data by calling the api
     * and this updated data will be obsereved by Observer
     */
    override fun onRestart() {
        super.onRestart()
        mConfereenceRoomViewModel.getConferenceRoomList(this, setDataToObjectForApiCall(mIntentDataFromActvity))
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    fun setDataToObjectForApiCall(mIntentDataFromActvity: GetIntentDataFromActvity): FetchConferenceRoom {
        var mFetchRoom = FetchConferenceRoom()
        mFetchRoom.fromTime = mIntentDataFromActvity.fromtime
        mFetchRoom.toTime = mIntentDataFromActvity.totime
        mFetchRoom.capacity = mIntentDataFromActvity.capacity!!.toInt()
        mFetchRoom.buildingId = mIntentDataFromActvity.buildingId!!.toInt()
        return mFetchRoom
    }

    fun showDialog() {
        val mDialog = GetAleretDialog.getDialog(this, "Status", "No Room Available")
        mDialog.setPositiveButton(getString(R.string.ok)) { dialog, which ->
            finish()
        }
        GetAleretDialog.showDialog(mDialog)
    }
}
