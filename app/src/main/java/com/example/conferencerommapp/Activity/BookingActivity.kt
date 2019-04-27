package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
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
//import com.example.conferencerommapp.Helper.CheckBoxAdapter
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_booking.*
import kotlinx.android.synthetic.main.activity_spinner.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class BookingActivity : AppCompatActivity() {

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
    private lateinit var mBookingViewModel: BookingViewModel
    private var mBooking = Booking()
    lateinit var progressDialog: ProgressDialog
    private lateinit var acct: GoogleSignInAccount
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>")

        val mIntentDataFromActivity = getIntentData()
        init()
        observerData()
        setDataToTextView(mIntentDataFromActivity, acct.displayName.toString())
        addDataToObject(mIntentDataFromActivity)
    }

    /**
     * this method will Initialize all input fields
     */

    private fun init() {
        progressDialog =
            GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        acct = GoogleSignIn.getLastSignedInAccount(applicationContext)!!
        // getting view model object
        mBookingViewModel = ViewModelProviders.of(this).get(BookingViewModel::class.java)
        textChangeListenerOnPurposeEditText()
    }

    /**
     * function sets a observer which will observe the data from ViewModel
     */
    private fun observerData() {
        // positive response from server
        mBookingViewModel.returnSuccessForBooking().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show();
            goToBookingDashboard()
        })
        // negative response from server
        mBookingViewModel.returnFailureForBooking().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
            }
        })
    }

    /**
     * function invoked automatically when user hit the book button
     */
    @OnClick(R.id.book_button)
    fun bookMeeting() {
        if (validateInput() and validatePurposeRegrex()) {
            addBooking(mBooking)
        }
    }

    /**
     * set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mBookingDetails: GetIntentDataFromActvity) {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        mBooking.email = acct!!.email
        mBooking.roomId = mBookingDetails.roomId!!.toInt()
        mBooking.buildingId = mBookingDetails.buildingId!!.toInt()
        mBooking.fromTime = mBookingDetails.fromTime!!
        mBooking.toTime = mBookingDetails.toTime!!
        mBooking.roomName = mBookingDetails.roomName!!
        mBooking.cCMail = mBookingDetails.emailOfSelectedEmployees
    }




    /**
     * validate all input fields
     */
    private fun validateInput(): Boolean {
        return if (purposeEditText.text.toString().trim().isEmpty()) {
            purpose_edit_layout.error = getString(R.string.field_cant_be_empty)
            false
        }else {
            purpose_edit_layout.error = null
            true
        }
    }
    private fun validatePurposeRegrex():Boolean{

        val purposePattern: String = "^[A-Za-z]+"
        val pattern: Pattern = Pattern.compile(purposePattern)
        val matcher: Matcher = pattern.matcher(purposeEditText.text)
        //        return matcher.matches()
        return if(!matcher.matches()){
            purpose_edit_layout.error = getString(R.string.invalid_purpose_name)
            false
        }
        else{
            true
        }
    }

    /**
     * get that data from intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }


    /**
     * function will set the data into textview
     */
    @SuppressLint("SetTextI18n")
    fun setDataToTextView(mBookingDetails: GetIntentDataFromActvity, userName: String) {
        fromTimeTextView.text =
            mBookingDetails.fromTime!!.split(" ")[1] + " - " + mBookingDetails.toTime!!.split(" ")[1]
        dateTextView.text = FormatDate.formatDate(mBookingDetails.date!!)
        roomNameTextView.text = mBookingDetails.roomName!!
        buildingNameTextView.text = mBookingDetails.buildingName!!
        employeeNameTextView.text = userName
    }


    /**
     *  calls the function of view model to get the data from server
     */
    private fun addBooking(mBooking: Booking) {
        mBooking.purpose = purposeEditText.text.toString()
        progressDialog.show()
        mBookingViewModel.addBookingDetails(mBooking, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     *  redirect to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
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

    /**
     * add text change listener for the purpose edit text
     */
    private fun textChangeListenerOnPurposeEditText() {
        purposeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInput()
            }
        })
    }
}