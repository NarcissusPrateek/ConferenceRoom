package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.UpdateBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.UpdateBookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import es.dmoral.toasty.Toasty





class UpdateBookingActivity : AppCompatActivity() {

    private lateinit var mUpdateBookingViewModel: UpdateBookingViewModel
    private var mUpdateBooking = UpdateBooking()
    private var oldFromTime: String? = null
    private var oldToTime: String? = null

    @BindView(R.id.Purpose)
    lateinit var purpose: EditText

    @BindView(R.id.fromTime_update)
    lateinit var newFromTime: EditText

    @BindView(R.id.toTime_update)
    lateinit var newToTime: EditText

    @BindView(R.id.date_update)
    lateinit var date: EditText

    @BindView(R.id.buildingname)
    lateinit var buildingName: EditText

    @BindView(R.id.conferenceRoomName)
    lateinit var roomName: EditText

    private lateinit var progressDialog: ProgressDialog

    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_booking)
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.update) + "</font>")
        ButterKnife.bind(this)
        mIntentDataFromActivity = getIntentData()
        init()
        observerData()
        setStatusOfBooking(mIntentDataFromActivity.bookingId)
        setValuesInEditText(mIntentDataFromActivity)
        setEditTextPicker()
    }

    // call method of view model
    private fun setStatusOfBooking(bookingId: Int?) {
        progressDialog.show()
        mUpdateBookingViewModel.changeStatus(bookingId!!, getUserIdFromPreference(), getTokenFromPreference())
    }

    private fun addDataToObjects(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        mUpdateBooking.email = acct!!.email
        mUpdateBooking.cCMail = mIntentDataFromActivity.cCMail!!.joinToString()
        mUpdateBooking.roomId = mIntentDataFromActivity.roomId!!.toInt()
        mUpdateBooking.roomName = mIntentDataFromActivity.roomName!!
        mUpdateBooking.newfromTime = (date.text.toString() + " " + newFromTime.text.toString()).trim()
        mUpdateBooking.newtotime = (date.text.toString() + " " + newToTime.text.toString()).trim()
        mUpdateBooking.fromTime = (date.text.toString() + " " + oldFromTime.toString()).trim()
        mUpdateBooking.toTime = (date.text.toString() + " " + oldToTime.toString()).trim()
    }

    @OnClick(R.id.update)
    fun updateMeeting() {
        addDataToObjects(mIntentDataFromActivity)
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
            val startTime = newFromTime.text.toString()
            val endTime = newToTime.text.toString()

            /**
             * setting a aalert dialog instance for the current context
             */
            try {

                /**
                 * getting the values for time validation variables from method calculateTimeInMillis
                 */
                val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
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
                Toast.makeText(this@UpdateBookingActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun updateMeetingDetails() {
        progressDialog.show()
        mUpdateBookingViewModel.updateBookingDetails(mUpdateBooking, getUserIdFromPreference(),getTokenFromPreference())
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mUpdateBookingViewModel = ViewModelProviders.of(this).get(UpdateBookingViewModel::class.java)
    }

    /**
     * observing data for update booking
     */
    private fun observerData() {
        mUpdateBookingViewModel.returnBookingUpdated().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.booking_updated), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mUpdateBookingViewModel.returnUpdateFailed().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
            }
        })

        mUpdateBookingViewModel.returnStatusChanged().observe(this, Observer {
            progressDialog.dismiss()
        })

        mUpdateBookingViewModel.returnStatusChangeFailed().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
            }
            finish()
        })
    }

    private fun validate(): Boolean {

        if (TextUtils.isEmpty(newFromTime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_from_time), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(newToTime.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_to_time), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setEditTextPicker() {
        newFromTime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newFromTime)
        }

        /**
         * set Time picker for the edittext toTime
         */
        newToTime.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, newToTime)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setValuesInEditText(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val simpleDateFormatForTime = java.text.SimpleDateFormat("HH:mm:ss")
        val simpleDateFormatForTime1 = java.text.SimpleDateFormat("HH:mm")
        val mdate = mIntentDataFromActivity.date!!
        val mfromtime = mIntentDataFromActivity.fromTime!!.split("T")
        oldFromTime = mfromtime[1]
        val mtotime = mIntentDataFromActivity.toTime!!.split("T")
        oldToTime = mtotime[1]
        purpose.text = mIntentDataFromActivity.purpose!!.toEditable()
        newFromTime.text = simpleDateFormatForTime1.format(simpleDateFormatForTime.parse(mfromtime[1]))
            .toEditable()//oldFromTime.toString().toEditable()
        newToTime.text = simpleDateFormatForTime1.format(simpleDateFormatForTime.parse(mtotime[1])).toEditable()
        date.text = FormatDate.formatDate(mdate).toEditable()
        buildingName.text = mIntentDataFromActivity.buildingName!!.toEditable()
        roomName.text = mIntentDataFromActivity.roomName!!.toEditable()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        mUpdateBookingViewModel.changeStatus(getIntentData().bookingId!!, getUserIdFromPreference(), getTokenFromPreference())
    }


    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)


    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        mUpdateBookingViewModel.changeStatus(getIntentData().bookingId!!, getUserIdFromPreference(), getTokenFromPreference())
        startActivity(Intent(this@UpdateBookingActivity, UserBookingsDashboardActivity::class.java))
        finish()
        return true
    }
}
