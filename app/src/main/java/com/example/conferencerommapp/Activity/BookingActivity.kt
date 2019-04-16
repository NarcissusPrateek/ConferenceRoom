package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
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
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
import kotlinx.android.synthetic.main.activity_user_inputs.*
import java.util.*

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
    @BindView(R.id.editText_person)
    lateinit var addPersonEditText: EditText
    private lateinit var mBookingViewModel: BookingViewModel
    private var names = ArrayList<EmployeeList>()
    private var customAdapter: CheckBoxAdapter? = null
    private var checkedEmployee = ArrayList<EmployeeList>()
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
        setDialogForSelectingMeetingMembers()
        setDialog(mIntentDataFromActivity)
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
    }

    /**
     * observe data from server
     */
    private fun observerData() {

        // observer for fetching employee List from server
        mBookingViewModel.returnSuccessForBooking().observe(this, Observer {
            progressDialog.dismiss()
            goToBookingDashboard()
        })
        mBookingViewModel.returnFailureForBooking().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })

        // observer for add booking details
        mBookingViewModel.returnSuccessForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            names.clear()
            for (item in it!!) {
                item.isSelected = false
                names.add(item)
            }
        })
        mBookingViewModel.returnFailureForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })
    }

    /**
     * show toast message according to the error
     */
    private fun showToastAccordingToError(message: String) {
        if(message == getString(R.string.internal_server)) {
            Toasty.error(this, message, Toast.LENGTH_SHORT, true).show()
        }else {
            Toasty.info(this, message, Toast.LENGTH_SHORT, true).show()
        }
    }

    /**
     * function invoked automatically when user hit the book button
     */
    @OnClick(R.id.book_button)
    fun bookMeeting() {
        if (validateInput()) {
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
    }

    private fun validatePerposer(): Boolean {
        var input = purposeEditText.text.toString().trim()
        return if (input.isEmpty()) {

            false
        } else {
            from_time_layout.error = null
            from_time_layout.isErrorEnabled = false
            true
        }
    }




    /**
     * validate all input fields
     */
    private fun validateInput(): Boolean {
        if (purposeEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Invalid purpose!", Toast.LENGTH_SHORT).show()
            return false
        } else if (addPersonEditText.text.trim().isEmpty()) {
            Toast.makeText(this, "Add Members!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * this function get the filtered data and according to the data set the data into adapter
     */
    fun filter(text: String) {
        val filterName = ArrayList<EmployeeList>()
        for (s in names) {

            if (s.name!!.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }
        customAdapter!!.filterList(filterName)
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
        dateTextView.text = mBookingDetails.date!!
        roomNameTextView.text = mBookingDetails.roomName!!
        buildingNameTextView.text = mBookingDetails.buildingName!!
        employeeNameTextView.text = userName
    }

    /**
     * function will make a api call whcih will get all the employee list from backend
     */
    private fun setDialogForSelectingMeetingMembers() {
        progressDialog.show()
        mBookingViewModel.getEmployeeList()
    }


    /**
     * set alert dialog to diaplay all the employee name list and provides option to select employee for meeting
     */
    @SuppressLint("InflateParams")
    fun setDialog(mBookingDetails: GetIntentDataFromActvity) {
        val mBuilder = AlertDialog.Builder(this@BookingActivity)
        mBuilder.setTitle("Select atmax ${mBookingDetails.capacity} members.")
        mBuilder.setCancelable(false)
        addPersonEditText.setOnClickListener {
            customAdapter = CheckBoxAdapter(names, checkedEmployee, this@BookingActivity)
            val view = layoutInflater.inflate(R.layout.activity_alertdialog_members, null)
            view.recycler_view.adapter = customAdapter
            view.editTextSearch.onRightDrawableClicked {
                it.text.clear()
            }
            setClickListenerOnEditText(view)
            mBuilder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                var email = ""
                var name = ""
                val employeeList = customAdapter!!.getList()
                val size = employeeList.size
                employeeList.indices.forEach { item ->
                    if (employeeList[item].isSelected!!) {
                        name += employeeList[item].name.toString()
                        email += employeeList[item].email.toString()
                        if (item != (size - 1)) {
                            name += ","
                            email += ","
                        }
                    }
                }
                addPersonEditText.setText(name)
                mBooking.cCMail = email

            }
            mBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface?, _: Int ->
                addPersonEditText.setText("")
                mBooking.cCMail = ""
            }
            mBuilder.setView(view)
            val builder = mBuilder.create()
            builder.show()
            ColorOfDialogButton.setColorOfDialogButton(builder)
        }
    }

    /**
     * attach a addTextChangedListener which will search data into the list
     */
    private fun setClickListenerOnEditText(view: View) {
        view.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing Here
                 */
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing here
                 */
            }

            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
    }

    /**
     * function sets a observer which will observe the data from backend and add the booking details to the database
     */
    private fun addBooking(mBooking: Booking) {
        mBooking.purpose = purposeEditText.text.toString()
        progressDialog.show()
        mBookingViewModel.addBookingDetails(mBooking)
    }

    /**
     *  redirect to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {

        Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show();
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }
}