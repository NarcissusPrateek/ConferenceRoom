package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
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
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.ManagerBooking
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ManagerBookingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_alertdialog_members.view.*
import java.util.*

@Suppress("DEPRECATION")
class ManagerBookingActivity : AppCompatActivity() {

    private var names = ArrayList<EmployeeList>()
    private var customAdapter: CheckBoxAdapter? = null
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
    private lateinit var mManagerBookingViewModel: ManagerBookingViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var acct: GoogleSignInAccount
    private var checkedEmployee = ArrayList<EmployeeList>()
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
        getDateForSelectingMeetingMembers()
        setDialog()
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
        mManagerBooking.roomId = mGetIntentDataFromActvity.roomId!!.toInt()
        mManagerBooking.buildingId = mGetIntentDataFromActvity.buildingId!!.toInt()
        mManagerBooking.fromTime = mGetIntentDataFromActvity.fromTimeList
        mManagerBooking.toTime = mGetIntentDataFromActvity.toTimeList
    }


    /**
     * validate all input fields
     */
    private fun validateInput(): Boolean {
        if (purposeEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_purpose), Toast.LENGTH_SHORT).show()
            return false
        } else if (addPersonEditText.text.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.add_members), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
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
     * function will make a api call whcih will get all the employee list from backend
     */
    private fun getDateForSelectingMeetingMembers() {
        progressDialog.show()
        mManagerBookingViewModel.getEmployeeList()

    }

    /**
     * observe data from server
     */
    private fun observeData() {
        // observer for employeeList
        mManagerBookingViewModel.returnSuccessForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            names.clear()
            for (item in it!!) {
                item.isSelected = false
                names.add(item)
            }
        })
        mManagerBookingViewModel.returnFailureForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })

        // observer for add Booking
        mManagerBookingViewModel.returnSuccessForBooking().observe(this, Observer {
            progressDialog.dismiss()
            goToBookingDashboard()
        })
        mManagerBookingViewModel.returnFailureForBooking().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })
    }

    /**
     * set alert dialog to diaplay all the employee name list and provides option to select employee for meeting
     */
    @SuppressLint("InflateParams")
    private fun setDialog() {
        val mBuilder = android.app.AlertDialog.Builder(this@ManagerBookingActivity)
        mBuilder.setTitle(getString(R.string.select_members))
        mBuilder.setCancelable(false)
        addPersonEditText.setOnClickListener {
            customAdapter = CheckBoxAdapter(names, checkedEmployee, this@ManagerBookingActivity)
            val view = layoutInflater.inflate(R.layout.activity_alertdialog_members, null)
            view.recycler_view.adapter = customAdapter
            setClickListnerOnEditText(view)
            mBuilder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                var email = ""
                var name = ""
                val employeeList = customAdapter!!.getList()
                val size = employeeList.size
                for (item in employeeList.indices) {
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
                mManagerBooking.cCMail = email

            }
            mBuilder.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface?, _: Int ->
                addPersonEditText.setText("")
                mManagerBooking.cCMail = ""
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
                 * Nothing Here
                 */
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
        val filterNames = ArrayList<EmployeeList>()
        for (s in names) {
            if (s.name!!.toLowerCase().contains(text.toLowerCase())) {
                filterNames.add(s)
            }
        }
        customAdapter!!.filterList(filterNames)
    }

    /**
     * function sets a observer which will observe the data from backend and add the booking details to the database
     */
    private fun addBooking() {
        progressDialog.show()
        mManagerBookingViewModel.addBookingDetails(mManagerBooking)
    }

    /**
     * go to UserBookingDashboardActivity
     */
    private fun goToBookingDashboard() {
        Toasty.success(this, getString(R.string.booked_successfully), Toast.LENGTH_SHORT, true).show()
        startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        finish()

    }
}















