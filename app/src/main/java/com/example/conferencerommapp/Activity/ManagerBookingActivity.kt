package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.conferencerommapp.Helper.CheckBoxAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Booking
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
import kotlinx.android.synthetic.main.activity_booking.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ManagerBookingActivity : AppCompatActivity() {

    lateinit var empList: List<EmployeeList>
    var progressDialog: ProgressDialog? = null
    var mGoogleSignInClient: GoogleSignInClient? = null
    var names = ArrayList<EmployeeList>()
    var customAdapter: CheckBoxAdapter? = null
    var checkedEmployee = ArrayList<EmployeeList>()
    val mBuilder = android.app.AlertDialog.Builder(this@ManagerBookingActivity)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Confirm_Details) + "</font>"))

        val txv_fromTime: TextView = findViewById(R.id.textView_from_time)
        val txvDate: TextView = findViewById(R.id.textView_date)
        val txvemployeename: TextView = findViewById(com.example.conferencerommapp.R.id.textView_name)
        var edittextPurpose = findViewById(com.example.conferencerommapp.R.id.editText_purpose) as EditText
        val txvBildingname: TextView = findViewById(R.id.textView_buildingname)

        val bundle: Bundle = intent.extras
        var fromtime = bundle.getStringArrayList(Constants.EXTRA_FROM_TIME_LIST)
        var totime = bundle.getStringArrayList(Constants.EXTRA_TO_TIME_LIST)
        var date = bundle!!.get(Constants.EXTRA_DATE).toString()
        var toDate = bundle!!.get(Constants.EXTRA_TO_DATE).toString()
        var roomname = bundle!!.get(Constants.EXTRA_ROOM_NAME).toString()
        var buildingname = bundle!!.get(Constants.EXTRA_BUILDING_NAME).toString()
        var cid = bundle!!.get(Constants.EXTRA_ROOM_ID).toString()
        var bid = bundle!!.get(Constants.EXTRA_BUILDING_ID).toString()
        var count = 0

        txv_fromTime.text = fromtime[0].split(" ")[1] + " - " + totime[0].split(" ")[1]
        txvDate.text = date + " - " + toDate
        txvBildingname.text = buildingname

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)

        val booking = ManagerBooking()
        booking.Email = acct!!.email
        booking.CId = cid.toInt()
        booking.BId = bid.toInt()
        booking.FromTime = fromtime
        booking.ToTime = totime
        txvemployeename.text = acct!!.displayName

        getEmployeeNameList()

        editText_person.setOnClickListener {
                selectEmployeesForMeeting(booking)
        }
        book_button.setOnClickListener {

            if (edittextPurpose.text.toString().trim().isEmpty()) {
                Toast.makeText(this@ManagerBookingActivity, "Enter the purpose of meeting.", Toast.LENGTH_SHORT).show()
            } else if (booking.CCMail.isNullOrEmpty()) {
                Toast.makeText(this@ManagerBookingActivity, "Select members for meeting!.", Toast.LENGTH_SHORT).show()
            } else if (count > 0) {
                Toast.makeText(
                    this@ManagerBookingActivity,
                    "Select according to the room capacity.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                progressDialog = ProgressDialog(this@ManagerBookingActivity)
                progressDialog!!.setMessage("Processing....")
                progressDialog!!.setCancelable(false)
                progressDialog!!.show()
                booking.Purpose = edittextPurpose.text.toString()
                booking.CName = roomname
                addBookingDetails(booking, booking.Email.toString())
            }
        }
    }

    private fun addBookingDetails(booking: ManagerBooking, email: String) {
        val service = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = service.addManagerBookingDetails(booking)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog!!.dismiss()
                Log.i("-----------", t.message)
                Toast.makeText(this@ManagerBookingActivity, "Error on Failure", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("-------#####-----", response.toString())
                progressDialog!!.dismiss()
                if (response.isSuccessful) {
                    val code = response.body()
                    val intent = Intent(Intent(this@ManagerBookingActivity, Main2Activity::class.java))
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@ManagerBookingActivity, "Unable to Book.... ", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
    fun getEmployeeNameList() {
        var servicebuilder = Servicebuilder.buildService(ConferenceService::class.java)
        var requestCall = servicebuilder.getEmployees()
        requestCall.enqueue(object : Callback<List<EmployeeList>> {
            override fun onFailure(call: Call<List<EmployeeList>>, t: Throwable) {
                Toast.makeText(
                    this@ManagerBookingActivity,
                    "On failure while Loading the EmployeeList",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            override fun onResponse(call: Call<List<EmployeeList>>, response: Response<List<EmployeeList>>) {
                Log.i("----Response--------", response.body().toString())
                var empList = response.body()
                for (item in empList!!) {
                    item.isSelected = false
                    names.add(item)
                }
                Log.i("-------", names.toString())
            }
        })
    }
    fun selectEmployeesForMeeting(booking: ManagerBooking) {
        customAdapter = CheckBoxAdapter(names, checkedEmployee, this@ManagerBookingActivity)
        var view = layoutInflater.inflate(R.layout.activity_alertdialog_members, null)
        view.clear_Text.setOnClickListener {
            view.editTextSearch.setText("")
        }
        view.recycler_view.adapter = customAdapter

        //make Edittext Searchable

        editTextSearch(view)

        mBuilder.setPositiveButton("Ok") { dialogInterface, which ->
            var email = ""
            var name = ""
            Log.i("-------List of Employee", customAdapter!!.getList().toString())
            var EmployeeList = customAdapter!!.getList()
            var size = EmployeeList.size
            for (item in EmployeeList.indices) {
                if (EmployeeList.get(item).isSelected!!) {
                    name = name + EmployeeList[item].Name.toString()
                    email = email + EmployeeList[item].Email.toString()
                    if (item != (size - 1)) {
                        name += ","
                        email += ","
                    }
                }
            }
            editText_person.setText(name)
            booking.CCMail = email
            Log.i("-------------", name)
            Log.i("-------------", booking.CCMail)
        }
        mBuilder.setNegativeButton("Cancel") { dialog: DialogInterface?, which: Int ->
            editText_person.setText("")
            booking.CCMail = ""
        }
        mBuilder.setView(view)
        mBuilder.setCancelable(false)
        var builder = mBuilder.create()
        builder.show()
        var buttonPositive = builder.getButton(AlertDialog.BUTTON_POSITIVE)
        var buttonNegative = builder.getButton(AlertDialog.BUTTON_NEGATIVE)


        buttonPositive.setBackgroundColor(Color.WHITE)
        buttonPositive.setTextColor(Color.parseColor("#0072BC"))

        buttonNegative.setBackgroundColor(Color.WHITE)
        buttonNegative.setTextColor(Color.RED)
    }
    //Update the recyclerview with particular search item
    fun filter(text: String) {
        val filterdNames = ArrayList<EmployeeList>()
        for (s in names) {
            if (s.Name!!.toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s)
            }
        }
        customAdapter!!.filterList(filterdNames)
    }

    // to make edittext searchable
    fun editTextSearch(view: View) {
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
}