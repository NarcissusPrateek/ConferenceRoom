package com.example.conferencerommapp.Activity

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
import com.example.conferencerommapp.Helper.ConferenceRoomAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.FetchConferenceRoom
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ConferenceRoomViewModel

@Suppress("DEPRECATION")
class ConferenceRoomActivity : AppCompatActivity() {

    private lateinit var mConferenceRoomViewModel: ConferenceRoomViewModel
    private lateinit var mCustomAdapter: ConferenceRoomAdapter
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
    private fun getViewModel(mIntentDataFromActivity: GetIntentDataFromActvity, mFetchRoom: FetchConferenceRoom) {
        /**
         * get progress dialog
         */
        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mConferenceRoomViewModel = ViewModelProviders.of(this).get(ConferenceRoomViewModel::class.java)
        progressDialog.show()
        mConferenceRoomViewModel.getConferenceRoomList(mFetchRoom)
        mConferenceRoomViewModel.returnSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if(it.isEmpty()) {
                showAlertDialog()
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
        mConferenceRoomViewModel.returnFailure().observe(this, Observer {
            progressDialog.dismiss()
            // according to the code
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
            finish()
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
        mConferenceRoomViewModel.getConferenceRoomList(setDataToObjectForApiCall(mIntentDataFromActivity))
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
     * show alert dialog when rooms are not available
     */
    fun showAlertDialog() {
        val mDialog = GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.room_not_available))
        mDialog.setPositiveButton(getString(R.string.ok)) {_,_->
            finish()
        }
        GetAleretDialog.showDialog(mDialog)
    }
}
