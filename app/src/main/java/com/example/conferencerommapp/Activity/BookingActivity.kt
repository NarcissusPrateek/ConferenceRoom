package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
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
import com.example.conferencerommapp.Helper.CheckBoxAdapter
import com.example.conferencerommapp.Helper.ColorOfDialogButton
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BookingViewModel
import com.example.conferencerommapp.ViewModel.EmployeeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
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
    private lateinit var mEmployeeViewModel: EmployeeViewModel
    private lateinit var mBookingViewModel: BookingViewModel
    private var names = ArrayList<EmployeeList>()
    private var customAdapter: CheckBoxAdapter? = null
    private var checkedEmployee = ArrayList<EmployeeList>()
    private var mBooking = Booking()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>")


        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        val mIntentDataFromActivity = getIntentData()
        setDataToTextview(mIntentDataFromActivity, acct!!.displayName.toString())
        setDialogForSelectingMeetingMembers()
        setDialog(mIntentDataFromActivity)
        addDataToObject(mIntentDataFromActivity)
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
        mBooking.fromTime = mBookingDetails.fromtime!!
        mBooking.toTime = mBookingDetails.totime!!
        mBooking.roomName = mBookingDetails.roomName!!
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
        val filterdNames = ArrayList<EmployeeList>()
        for (s in names) {

            if (s.name!!.toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s)
            }
        }
        customAdapter!!.filterList(filterdNames)
    }

    /**
     * this method will Initialize all input fields
     */


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
    fun setDataToTextview(mBookingDetails: GetIntentDataFromActvity, userName: String) {
        fromTimeTextView.text =
            mBookingDetails.fromtime!!.split(" ")[1] + " - " + mBookingDetails.totime!!.split(" ")[1]
        dateTextView.text = mBookingDetails.date!!
        roomNameTextView.text = mBookingDetails.roomName!!
        buildingNameTextView.text = mBookingDetails.buildingName!!
        employeeNameTextView.text = userName
    }

    /**
     * function will make a api call whcih will get all the employee list from backend
     */
    private fun setDialogForSelectingMeetingMembers() {
        mEmployeeViewModel = ViewModelProviders.of(this).get(EmployeeViewModel::class.java)
        mEmployeeViewModel.getEmployeeList(this).observe(this, Observer {
            names.clear()
            for (item in it!!) {
                item.isSelected = false
                names.add(item)
            }
        })
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
            view.clear_Text.setOnClickListener {
                view.editTextSearch.setText("")
            }
            setClickListnerOnEditText(view)
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
    private fun setClickListnerOnEditText(view: View) {
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
        mBookingViewModel = ViewModelProviders.of(this).get(BookingViewModel::class.java)
        mBookingViewModel.addBookingDetails(this, mBooking).observe(this, Observer {
            goToBookingDashboard()
        })
    }

    /**
     *  redirect to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {
        val mDialog = GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.booked_successfully))
        mDialog.setPositiveButton(getString(R.string.ok)) { _,_ ->
            startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
            finish()
        }
        GetAleretDialog.showDialog(mDialog)
    }
}
