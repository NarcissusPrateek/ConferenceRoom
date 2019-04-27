package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.ManagerBookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking.*

@Suppress("DEPRECATION")
class ManagerBookingActivity : AppCompatActivity() {

    @BindView(R.id.textView_from_time)
    lateinit var fromTimeTextView: TextView
    @BindView(R.id.textView_date)
    lateinit var dateTextView: TextView
    @BindView(R.id.textView_conf_name)
    lateinit var roomNameTextView: TextView
    @BindView(R.id.textView_name)
    lateinit var employeeNameTextView: TextView
    @BindView(R.id.editText_purpose)
    lateinit var purposeEditText: EditText
    @BindView(R.id.textView_buildingname)
    lateinit var buildingNameTextView: TextView
    private lateinit var mManagerBookingViewModel: ManagerBookingViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var acct: GoogleSignInAccount
    private var mManagerBooking = ManagerBooking()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>")
        acct = GoogleSignIn.getLastSignedInAccount(applicationContext)!!


        val mGetIntentDataFromActvity = getIntentData()
        init()
        observeData()
        setDataToTextview(mGetIntentDataFromActvity, acct.displayName.toString())
        addDataToObject(mGetIntentDataFromActvity)
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mManagerBookingViewModel = ViewModelProviders.of(this).get(ManagerBookingViewModel::class.java)
    }

    @OnClick(R.id.book_button)
    fun bookMeeting() {
        if (validateInput()) {
            mManagerBooking.roomName = getIntentData().roomName
            mManagerBooking.purpose = purposeEditText.text.toString()
            addBooking()
        }
    }

    /**
     * set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        mManagerBooking.email = acct.email
        mManagerBooking.cCMail = mGetIntentDataFromActvity.emailOfSelectedEmployees
        mManagerBooking.roomId = mGetIntentDataFromActvity.roomId!!.toInt()
        mManagerBooking.buildingId = mGetIntentDataFromActvity.buildingId!!.toInt()
        mManagerBooking.fromTime = mGetIntentDataFromActvity.fromTimeList
        mManagerBooking.toTime = mGetIntentDataFromActvity.toTimeList
    }


    /**
     * validate all input fields
     */
    private fun validateInput(): Boolean {
        return if (purposeEditText.text.toString().trim().isEmpty()) {
            purpose_edit_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            purpose_edit_layout.error = null
            true
        }

    }

    /**
     * get data from intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * attach a addTextChangedListener which will search data into the list
     */
    @SuppressLint("SetTextI18n")
    private fun setDataToTextview(mGetIntentDataFromActvity: GetIntentDataFromActvity, userName: String) {
        fromTimeTextView.text = mGetIntentDataFromActvity.fromTime + " - " + mGetIntentDataFromActvity.toTime
        dateTextView.text = mGetIntentDataFromActvity.date + " - " + mGetIntentDataFromActvity.toDate
        buildingNameTextView.text = mGetIntentDataFromActvity.buildingName
        roomNameTextView.text = mGetIntentDataFromActvity.roomName!!
        employeeNameTextView.text = userName
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        // observer for add Booking
        mManagerBookingViewModel.returnSuccessForBooking().observe(this, Observer {
            progressDialog.dismiss()
            goToBookingDashboard()
        })
        mManagerBookingViewModel.returnFailureForBooking().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
                finish()
            }
        })
    }


    /**
     * function sets a observer which will observe the data from backend and add the booking details to the database
     */
    private fun addBooking() {
        progressDialog.show()
        mManagerBookingViewModel.addBookingDetails(mManagerBooking, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * go to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {
        Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(
            this, getString(R.string.session_expired), "Your session is expired!\n" +
                    getString(R.string.session_expired_messgae)
        )
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















