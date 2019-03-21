package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Helper.CheckBoxAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.BookingDetails
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BookingViewModel
import com.example.conferencerommapp.ViewModel.EmployeeViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
import java.util.*

class BookingActivity : AppCompatActivity() {

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
    lateinit var mBookingViewModel: BookingViewModel
    lateinit var addPersonEdittext: EditText
    var checkedEmployee = ArrayList<EmployeeList>()
    var mBooking = Booking()


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>"))

        // this method will Initialize all input fields
        initializeInputFields()
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        var mBookingDetails = getIntentData()
        setDataToTextview(mBookingDetails, acct!!.displayName.toString())
        setDialogForSelectingMeetingMembers()
        setDialog(mBookingDetails)
        addDataToObject(mBookingDetails)
        bookButton.setOnClickListener {
            if (validateInput()) {
                addBooking(mBooking)
            }
        }
    }

    fun addDataToObject(mBookingDetails: BookingDetails) {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        Log.i("----email---", acct!!.email.toString())
        mBooking.Email = acct!!.email
        mBooking.CId = mBookingDetails.cId!!.toInt()
        mBooking.BId = mBookingDetails.bId!!.toInt()
        mBooking.FromTime = mBookingDetails.fromTime!!
        mBooking.ToTime = mBookingDetails.toTime!!
        mBooking.CName = mBookingDetails.roomName!!
    }

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

    fun filter(text: String) {
        val filterdNames = ArrayList<EmployeeList>()
        for (s in names) {

            if (s.Name!!.toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s)
            }
        }
        customAdapter!!.filterList(filterdNames)
    }

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

    fun getIntentData(): BookingDetails {
        val bundle: Bundle? = intent.extras
        var mBookingDetails = BookingDetails()
        mBookingDetails.fromTime = bundle!!.get(Constants.EXTRA_FROM_TIME).toString()
        mBookingDetails.toTime = bundle.get(Constants.EXTRA_TO_TIME).toString()
        mBookingDetails.date = bundle.get(Constants.EXTRA_DATE).toString()
        mBookingDetails.roomName = bundle.get(Constants.EXTRA_ROOM_NAME).toString()
        mBookingDetails.buildingName = bundle.get(Constants.EXTRA_BUILDING_NAME).toString()
        mBookingDetails.capacity = bundle.get(Constants.EXTRA_CAPACITY).toString()
        mBookingDetails.cId = bundle.get(Constants.EXTRA_ROOM_ID).toString()
        mBookingDetails.bId = bundle.get(Constants.EXTRA_BUILDING_ID).toString()
        mBookingDetails.roomName = bundle.get(Constants.EXTRA_ROOM_NAME).toString()
        return mBookingDetails
    }

    fun setDataToTextview(mBookingDetails: BookingDetails, userName: String) {
        fromTimeTextview.text =
            mBookingDetails.fromTime!!.split(" ")[1] + " - " + mBookingDetails.toTime!!.split(" ")[1]
        dateTextview.text = mBookingDetails.date!!
        roomNameTextview.text = mBookingDetails.roomName!!
        buildingNameTextview.text = mBookingDetails.buildingName!!
        employeeNameTextview.text = userName
    }

    fun setDialogForSelectingMeetingMembers() {
        mEmployeeViewModel = ViewModelProviders.of(this).get(EmployeeViewModel::class.java)
        mEmployeeViewModel.getEmployeeList().observe(this, Observer {
            names.clear()
            for (item in it!!) {
                item.isSelected = false
                names.add(item)
            }
            Log.i("-------4545-----", names.toString())
        })
    }

    fun setDialog(mBookingDetails: BookingDetails) {
        val mBuilder = AlertDialog.Builder(this@BookingActivity)
        mBuilder.setTitle("Select atmax ${mBookingDetails.capacity} members.")
        mBuilder.setCancelable(false)
        addPersonEdittext.setOnClickListener {
            Log.i("-----------", names.toString())
            customAdapter = CheckBoxAdapter(names, checkedEmployee, this@BookingActivity)
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
                        name += EmployeeList[item].Name.toString()
                        email += EmployeeList[item].Email.toString()
                        if (item != (size - 1)) {
                            name += ","
                            email += ","
                        }
                    }
                }
                addPersonEdittext.setText(name)
                mBooking.CCMail = email

            }
            mBuilder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
                addPersonEdittext.setText("")
                mBooking.CCMail = ""
            }
            mBuilder.setView(view)
            var builder = mBuilder.create()
            builder.show()
            setBuilderConfiguration(builder)
        }
    }

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
    fun setBuilderConfiguration(builder: AlertDialog) {
        var buttonPositive = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        var buttonNegative = builder.getButton(AlertDialog.BUTTON_NEGATIVE)

        buttonPositive.setBackgroundColor(Color.WHITE)
        buttonPositive.setTextColor(Color.parseColor("#0072BC"))

        buttonNegative.setBackgroundColor(Color.WHITE)
        buttonNegative.setTextColor(Color.RED)
    }

    fun addBooking(mBooking: Booking) {
        mBooking.Purpose = purposeEdittext.text.toString()
        Log.i("------Booking object", mBooking.toString())
        mBookingViewModel = ViewModelProviders.of(this).get(BookingViewModel::class.java)
        mBookingViewModel.addBookingDetails(this, mBooking).observe(this, Observer {
            if (it == 200) {
                startActivity(Intent(this@BookingActivity, Main2Activity::class.java))
                finish()
            } else {
                Log.i("--------", "" + it)
            }
        })
    }
}
