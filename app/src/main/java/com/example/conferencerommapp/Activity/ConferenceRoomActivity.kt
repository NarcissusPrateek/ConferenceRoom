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
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel

class ConferenceRoomActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    lateinit var mConfereenceRoomViewModel: ConferenceRoomViewModel
    lateinit var customAdapter: ConferenceRoomAdapter
    lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>"))

        var mIntentDataFromActvity = getIntentData()

        var mFetchRoom = FetchConferenceRoom()
        mFetchRoom.FromTime = mIntentDataFromActvity.fromtime
        mFetchRoom.ToTime = mIntentDataFromActvity.totime
        mFetchRoom.Capacity = mIntentDataFromActvity.capacity!!.toInt()
        mFetchRoom.BId = mIntentDataFromActvity.buildingId!!.toInt()
        getViewModel(mIntentDataFromActvity, mFetchRoom)
    }
    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }
    fun getViewModel(mIntentDataFromActvity: GetIntentDataFromActvity, mFetchRoom: FetchConferenceRoom) {
        recyclerView = findViewById(R.id.conference_recycler_view)
        mConfereenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mConfereenceRoomViewModel.getConferenceRoomList(this, mFetchRoom).observe(this, Observer {
            customAdapter = ConferenceRoomAdapter(
                it!!,
                object : ConferenceRoomAdapter.BtnClickListener {
                    override fun onBtnClick(roomId: String?, roomName: String?) {
                        mIntentDataFromActvity.roomId = roomId
                        mIntentDataFromActvity.roomName = roomName
                        goToNextActivity(mIntentDataFromActvity)
                    }
                })
            recyclerView.adapter = customAdapter
        })
    }
    fun goToNextActivity(mIntentDataFromActvity: GetIntentDataFromActvity) {
        val intent = Intent(this@ConferenceRoomActivity, BookingActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActvity)
        startActivity(intent)
    }
}
