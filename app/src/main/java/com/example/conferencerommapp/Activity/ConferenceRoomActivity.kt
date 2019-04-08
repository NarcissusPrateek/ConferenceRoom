package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel

@Suppress("DEPRECATION")
class ConferenceRoomActivity : AppCompatActivity() {

    private lateinit var mConferenceRoomViewModel: ConferenceRoomViewModel
    lateinit var mCustomAdapter: ConferenceRoomAdapter
    @BindView(R.id.conference_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conference_room)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Select_Room) + "</font>")

        mIntentDataFromActivity = getIntentData()
        val mFetchRoom = setDataToObjectForApiCall(mIntentDataFromActivity)
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
    private fun getViewModel(mIntentDataFromActvity: GetIntentDataFromActvity, mFetchRoom: FetchConferenceRoom) {
        mConferenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        mConferenceRoomViewModel.getConferenceRoomList(this, mFetchRoom).observe(this, Observer {
            if(it.isEmpty()) {
                showDialog()
            }else {
                mCustomAdapter = ConferenceRoomAdapter(
                    it!!,
                    object : ConferenceRoomAdapter.BtnClickListener {
                        override fun onBtnClick(roomId: String?, roomname: String?) {
                            mIntentDataFromActvity.roomId = roomId
                            mIntentDataFromActvity.roomName = roomname
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
        mConferenceRoomViewModel.getConferenceRoomList(this, setDataToObjectForApiCall(mIntentDataFromActivity))
    }

    /**
     * function will set data for different properties of object of FetchConferenceRoom class
     * and it will return that object which is used as a parameter for api call
     */
    private fun setDataToObjectForApiCall(mIntentDataFromActvity: GetIntentDataFromActvity): FetchConferenceRoom {
        val mFetchRoom = FetchConferenceRoom()
        mFetchRoom.fromTime = mIntentDataFromActvity.fromTime
        mFetchRoom.toTime = mIntentDataFromActvity.toTime
        mFetchRoom.capacity = mIntentDataFromActvity.capacity!!.toInt()
        mFetchRoom.buildingId = mIntentDataFromActvity.buildingId!!.toInt()
        return mFetchRoom
    }

    /**
     * show alert dialog when rooms are not available
     */
    fun showDialog() {
        val mDialog = GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.room_not_available))
        mDialog.setPositiveButton(getString(R.string.ok)) {_,_->
            finish()
        }
        GetAleretDialog.showDialog(mDialog)
    }
}
