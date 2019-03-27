package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerConference
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ManagerConferenceRoomViewModel

class ManagerConferenceRoomActivity : AppCompatActivity() {


    private lateinit var mManagerConferenceRoomViewModel: ManagerConferenceRoomViewModel
    private lateinit var mCustomAdapter: ConferenceRoomAdapter
    private lateinit var mGetIntentDataFromActivity: GetIntentDataFromActvity
    @BindView(R.id.conference_recycler_view)
    private lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__conference__room)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))
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
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get the object of ViewModel class and by using this object we call the api and set the observer on the function
     */
    private fun getViewModel(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        mManagerConferenceRoomViewModel = ViewModelProviders.of(this).get(ManagerConferenceRoomViewModel::class.java)
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
            mRecyclerView.adapter = mCustomAdapter
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
            setDataToObjectForApiCall(mGetIntentDataFromActivity)
        )
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    private fun setDataToObjectForApiCall(mGetIntentDataFromActvity: GetIntentDataFromActvity): ManagerConference {
        var input = ManagerConference()
        input.FromTime = mGetIntentDataFromActvity.fromTimeList
        input.ToTime = mGetIntentDataFromActvity.toTimeList
        input.BId = mGetIntentDataFromActvity.buildingId!!.toInt()
        return input
    }
}

