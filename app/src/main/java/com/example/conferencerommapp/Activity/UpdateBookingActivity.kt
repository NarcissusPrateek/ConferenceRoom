package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.UpdateBookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn

class UpdateBookingActivity : AppCompatActivity() {

    lateinit var mUpdateBookingViewModel:UpdateBookingViewModel
    private var mUpdateBooking = UpdateBooking()
    var oldfromtime : String? = null
    var oldtotime: String? = null
    @BindView(R.id.Purpose)
    lateinit var purpose: EditText

    @BindView(R.id.fromTime_update)
    lateinit var newfromtime: EditText

    @BindView(R.id.toTime_update)
    lateinit var newtotime: EditText

    @BindView(R.id.date_update)
    lateinit var date: EditText

    @BindView(R.id.buildingname)
    lateinit var buildingname: EditText

    @BindView(R.id.conferenceRoomName)
    lateinit var roomname: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_booking)
        val mIntentDataFromActivity = getIntentData()
        ButterKnife.bind(this)
        setValuesInEditText(mIntentDataFromActivity)

        setEditTextPicker()
        addDataToObjects(mIntentDataFromActivity)
    }

    private fun addDataToObjects(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        mUpdateBooking.email=acct!!.email
        mUpdateBooking.cCMail = mIntentDataFromActivity.cCMail
        mUpdateBooking.roomId = mIntentDataFromActivity.roomId!!.toInt()
        mUpdateBooking.roomName = mIntentDataFromActivity.roomName!!
        mUpdateBooking.newfromTime = (date.text.toString() + " " + newfromtime.text.toString()).trim()
        mUpdateBooking.newtotime = (date.text.toString() + " " + newtotime.text.toString()).trim()
        mUpdateBooking.fromTime = (date.text.toString() + " " + oldfromtime.toString()).trim()
        mUpdateBooking.toTime = (date.text.toString() + " " + oldtotime.toString()).trim()
    }

    @OnClick(R.id.update)
    fun updateMeeting(){
        validationOnDataEnteredByUser()
    }

    private fun validationOnDataEnteredByUser() {
        /**
         * Validate each input field whether they are empty or not
         * If the field contains no values we show a toast to user saying that the value is invalid for particular field
         */
        if (validate()) {
            val minMilliseconds: Long = 900000
            val maxMilliseconds: Long = 14400000

            /**
             * Get the start and end time of meeting from the input fields
             */
            val startTime = newfromtime.text.toString()
            val endTime = newtotime.text.toString()

            /**
             * setting a aalert dialog instance for the current context
             */
            try {

                /**
                 * getting the values for time validation variables from method calculateTimeInMillis
                 */
                val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMiliis(
                    startTime,
                    endTime,
                    date.text.toString()
                )
                /**
                 * if the elapsed2 < 0 that means the from time is less than the current time. In that case
                 * we restrict the user to move forword and show some message in alert that the time is not valid
                 */

                if (elapsed2 < 0) {
                    val builder = GetAleretDialog.getDialog(
                        this,
                        getString(R.string.invalid),
                        getString(R.string.invalid_fromtime)
                    )
                    builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    }
                    GetAleretDialog.showDialog(builder)
                }
                /**
                 * if MIN_MILIISECONDS <= elapsed that means the meeting duration is more than 15 min
                 * if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
                 * if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
                 * if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
                 * if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
                 */
                else if ((minMilliseconds <= elapsed) && (maxMilliseconds >= elapsed)) {
                        updateMeetingDetails()
                } else {
                    val builder = GetAleretDialog.getDialog(
                        this,
                        getString(R.string.invalid),
                        getString(R.string.time_validation_message)
                    )

                    builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    }
                    GetAleretDialog.showDialog(builder)
                }
            } catch (e: Exception) {
                Toast.makeText(this@UpdateBookingActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateMeetingDetails() {
        mUpdateBookingViewModel = ViewModelProviders.of(this).get(UpdateBookingViewModel::class.java)
        mUpdateBookingViewModel.updateBookingDetails(this, mUpdateBooking).observe(this, Observer {
            if(it == Constants.OK_RESPONSE) {
                var builder = GetAleretDialog.getDialog(this,getString(R.string.status),"Successfully updated")
                builder.setPositiveButton(getString(R.string.ok)){_, _ ->
                    finish()
                }
                GetAleretDialog.showDialog(builder)
            }
        })
    }
    private fun validate(): Boolean {

        if (TextUtils.isEmpty(newfromtime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_from_time), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(newtotime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_to_time), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setEditTextPicker() {
        newfromtime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newfromtime)
        }

        /**
         * set Time picker for the edittext totime
         */
        newtotime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newtotime)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setValuesInEditText(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val simpleDateFormatForTime = java.text.SimpleDateFormat("HH:mm:ss")
        val simpleDateFormatForTime1 = java.text.SimpleDateFormat("HH:mm")
        Log.i("---",mIntentDataFromActivity.fromtime)
        val mdate = mIntentDataFromActivity.date!!
        val mfromtime = mIntentDataFromActivity.fromtime!!.split("T")
        oldfromtime = mfromtime[1]
        val mtotime = mIntentDataFromActivity.totime!!.split("T")
        oldtotime = mtotime[1]
        purpose.text = mIntentDataFromActivity.purpose!!.toEditable()
        newfromtime.text = simpleDateFormatForTime1.format(simpleDateFormatForTime.parse(mfromtime[1])).toEditable()//oldfromtime.toString().toEditable()
        newtotime.text = simpleDateFormatForTime1.format(simpleDateFormatForTime.parse(mtotime[1])).toEditable()
        date.text = mdate.toEditable()
        buildingname.text = mIntentDataFromActivity.buildingName!!.toEditable()
        roomname.text = mIntentDataFromActivity.roomName!!.toEditable()
    }


    private fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)


    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }
}
