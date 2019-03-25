package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerConference
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ManagerConferenceRoomViewModel

class ManagerConferenceRoomActivity : AppCompatActivity() {


    lateinit var mManagerConferenceRoomViewModel: ManagerConferenceRoomViewModel
    lateinit var mCustomAdapter: ConferenceRoomAdapter
    lateinit var mRecyclerview: RecyclerView
    lateinit var mGetIntentDataFromActvity: GetIntentDataFromActvity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__conference__room)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))
        mManagerConferenceRoomViewModel = ViewModelProviders.of(this).get(ManagerConferenceRoomViewModel::class.java)
        mRecyclerview = findViewById(R.id.conference_recycler_view)
        loadConferenceRoom()
    }

    fun loadConferenceRoom() {

        mGetIntentDataFromActvity = getIntentData()
        getViewModel(mGetIntentDataFromActvity)
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
    fun getViewModel(mGetIntentDataFromActvity: GetIntentDataFromActvity) {

        mManagerConferenceRoomViewModel.getConferenceRoomList(
            this,
            setDataToObjectForApiCall(mGetIntentDataFromActvity)
        ).observe(this, Observer {
            mCustomAdapter = ConferenceRoomAdapter(
                it!!,
                object : ConferenceRoomAdapter.BtnClickListener {
                    override fun onBtnClick(roomId: String?, roomName: String?) {
                        mGetIntentDataFromActvity.roomName = roomName
                        mGetIntentDataFromActvity.roomId = roomId
                        goToNextActivity(mGetIntentDataFromActvity)
                    }
                })
            mRecyclerview.adapter = mCustomAdapter
        })
    }


    /**
     * intent to the ManagerBookingActivity
     */
    fun goToNextActivity(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        val intent =
            Intent(this@ManagerConferenceRoomActivity, ManagerBookingActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActvity)
        startActivity(intent)
    }

    /**
     * onRestart of activity function will update the data by calling the api
     * and this updated data will be obsereved by Observer
     */
    override fun onRestart() {
        super.onRestart()
        mManagerConferenceRoomViewModel.getConferenceRoomList(
            this,
            setDataToObjectForApiCall(mGetIntentDataFromActvity)
        )
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    fun setDataToObjectForApiCall(mGetIntentDataFromActvity: GetIntentDataFromActvity): ManagerConference {
        var input = ManagerConference()
        input.FromTime = mGetIntentDataFromActvity.fromTimeList
        input.ToTime = mGetIntentDataFromActvity.toTimeList
        input.BId = mGetIntentDataFromActvity.buildingId!!.toInt()
        return input
    }
}

