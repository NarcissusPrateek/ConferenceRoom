package com.example.conferencerommapp.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Helper.CheckBoxAdapter
import com.example.conferencerommapp.Helper.ColorOfDialogButton
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.EmployeeViewModel
import com.example.conferencerommapp.ViewModel.ManagerBookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
import java.util.*

class ManagerBookingActivity : AppCompatActivity() {

    var names = ArrayList<EmployeeList>()
    var customAdapter: CheckBoxAdapter? = null
    lateinit var fromTimeTextview: TextView
    lateinit var dateTextview: TextView
    lateinit var roomNameTextview: TextView
    lateinit var employeeNameTextview: TextView
    lateinit var purposeEdittext: EditText
    lateinit var buildingNameTextview: TextView
    lateinit var bookButton: Button
    lateinit var mEmployeeViewModel: EmployeeViewModel
    lateinit var mManagerBookingViewModel: ManagerBookingViewModel
    lateinit var addPersonEdittext: EditText
    lateinit var acct: GoogleSignInAccount
    var checkedEmployee = ArrayList<EmployeeList>()
    var mManagerBooking = ManagerBooking()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>"))
        acct = GoogleSignIn.getLastSignedInAccount(applicationContext)!!

        initializeInputFields()

        var mGetIntentDataFromActvity = getIntentData()

        setDataToTextview(mGetIntentDataFromActvity, acct!!.displayName.toString())

        setDialogForSelectingMeetingMembers()
        setDialog(mGetIntentDataFromActvity)
        addDataToObject(mGetIntentDataFromActvity)

        bookButton.setOnClickListener {
            mManagerBooking.CName = mGetIntentDataFromActvity.roomName
            mManagerBooking.Purpose = purposeEdittext.text.toString()
            if (validateInput()) {
                addBooking()
            }
        }
    }

    /**
     * set values to the different properties of object which is required for api call
     */
    fun addDataToObject(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
        mManagerBooking.Email = acct!!.email
        mManagerBooking.CId = mGetIntentDataFromActvity.roomId!!.toInt()
        mManagerBooking.BId = mGetIntentDataFromActvity.buildingId!!.toInt()
        mManagerBooking.FromTime = mGetIntentDataFromActvity.fromTimeList
        mManagerBooking.ToTime = mGetIntentDataFromActvity.toTimeList
    }


    /**
     * validate all input fields
     */
    fun validateInput(): Boolean {
        if (purposeEdittext.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Invalid purpose!", Toast.LENGTH_SHORT).show()
            return false
        } else if (addPersonEdittext.text.trim().isEmpty()) {
            Toast.makeText(this, "Add Members!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    /**
     * this method will Initialize all input fields
     */
    fun initializeInputFields() {
        fromTimeTextview = findViewById(R.id.textView_from_time)
        dateTextview = findViewById(R.id.textView_date)
        roomNameTextview = findViewById(R.id.textView_conf_name)
        employeeNameTextview = findViewById(com.example.conferencerommapp.R.id.textView_name)
        purposeEdittext = findViewById(com.example.conferencerommapp.R.id.editText_purpose)
        addPersonEdittext = findViewById(R.id.editText_person)
        buildingNameTextview = findViewById(R.id.textView_buildingname)
        bookButton = findViewById(R.id.book_button)
    }


    /**
     * get data from intent
     */
    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * attach a addTextChangedListener which will search data into the list
     */
    fun setDataToTextview(mGetIntentDataFromActvity: GetIntentDataFromActvity, userName: String) {
        fromTimeTextview.text = mGetIntentDataFromActvity.fromtime + " - " + mGetIntentDataFromActvity.totime
        dateTextview.text = mGetIntentDataFromActvity.date + " - " + mGetIntentDataFromActvity.toDate
        buildingNameTextview.text = mGetIntentDataFromActvity.buildingName
        roomNameTextview.text = mGetIntentDataFromActvity.roomName!!
        employeeNameTextview.text = userName
    }


    /**
     * function will make a api call whcih will get all the employee list from backend
     */
    fun setDialogForSelectingMeetingMembers() {
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
    fun setDialog(mBookingDetails: GetIntentDataFromActvity) {
        val mBuilder = android.app.AlertDialog.Builder(this@ManagerBookingActivity)
        mBuilder.setTitle("Select atmax ${mBookingDetails.capacity} members.")
        mBuilder.setCancelable(false)
        addPersonEdittext.setOnClickListener {
            customAdapter = CheckBoxAdapter(names, checkedEmployee, this@ManagerBookingActivity)
            var view = layoutInflater.inflate(R.layout.activity_alertdialog_members, null)
            view.recycler_view.adapter = customAdapter
            view.clear_Text.setOnClickListener {
                view.editTextSearch.setText("")
            }
            setClickListnerOnEditText(view)
            mBuilder.setPositiveButton("Ok") { dialogInterface, which ->
                var email = ""
                var name = ""
                var EmployeeList = customAdapter!!.getList()
                var size = EmployeeList.size
                for (item in EmployeeList.indices) {
                    if (EmployeeList.get(item).isSelected!!) {
                        name += EmployeeList[item].name.toString()
                        email += EmployeeList[item].email.toString()
                        if (item != (size - 1)) {
                            name += ","
                            email += ","
                        }
                    }
                }
                addPersonEdittext.setText(name)
                mManagerBooking.CCMail = email

            }
            mBuilder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
                addPersonEdittext.setText("")
                mManagerBooking.CCMail = ""
            }
            mBuilder.setView(view)
            var builder = mBuilder.create()
            builder.show()
            ColorOfDialogButton.setColorOfDialogButton(builder)
        }
    }


    /**
     * attach a addTextChangedListener which will search data into the list
     */
    fun setClickListnerOnEditText(view: View) {
        view.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            }

            override fun afterTextChanged(editable: Editable) {
                filter(editable.toString())
            }
        })
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
     * function sets a observer which will observe the data from backend and add the booking details to the database
     */
    fun addBooking() {
        mManagerBookingViewModel = ViewModelProviders.of(this).get(ManagerBookingViewModel::class.java)
        mManagerBookingViewModel.addBookingDetails(this, mManagerBooking).observe(this, Observer {
            startActivity(Intent(this@ManagerBookingActivity, UserBookingsDashboardActivity::class.java))
            finish()
        })
    }
}















