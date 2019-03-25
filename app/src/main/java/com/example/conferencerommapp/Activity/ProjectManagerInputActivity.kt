package com.example.conferencerommapp.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.conferencerommapp.Helper.ColorOfDialogButton
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ConvertTimeInMillis
import com.example.conferencerommapp.Helper.DateAndTimePicker
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R

class ProjectManagerInputActivity : AppCompatActivity() {

    lateinit var fromTimeEditText: EditText
    lateinit var toTimeEditText: EditText
    lateinit var dateToEditText: EditText
    lateinit var dateFromEditText: EditText
    lateinit var buildingsActivityButton: Button
    lateinit var selectDaysButton: Button
    var listOfDays = ArrayList<Int>()
    var mUserItems = ArrayList<Int>()
    var listItems = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday", "Saturday")
    var checkedItems: BooleanArray = BooleanArray(listItems.size)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Booking Details" + "</font>"))

        initializeInputFields()
        setPickerToEdittextx()

        buildingsActivityButton.setOnClickListener {
            if (validate()) {
                applyValidationOnDateAndTime()
            }
        }
    }


    /**
     * initialize all input fields
     */
    fun initializeInputFields() {
        dateFromEditText = findViewById(R.id.date_manager)
        dateToEditText = findViewById(R.id.to_date_manager)
        fromTimeEditText = findViewById(R.id.fromTime_manager)
        toTimeEditText = findViewById(R.id.toTime_manager)
        buildingsActivityButton = findViewById(R.id.next_manager)
        selectDaysButton = findViewById(R.id.select_days)
    }

    /**
     * set date and time pickers to edittext fields
     */
    fun setPickerToEdittextx() {

        // set Time picker for the edittext fromtime
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }
        // set Time picker for the edittext totime
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }
        // set Date picker for the edittext dateEditText
        dateFromEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateFromEditText)
        }
        dateToEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateToEditText)
        }
        selectDaysButton.setOnClickListener(View.OnClickListener {
            getDays()
        })
    }

    /**
     * this function will select and store the days selected by user for recurring meeting
     */
    fun getDays() {
        val mBuilder = android.app.AlertDialog.Builder(this@ProjectManagerInputActivity)
        mBuilder.setMultiChoiceItems(listItems, checkedItems,
            DialogInterface.OnMultiChoiceClickListener { dialogInterface, position, isChecked ->
                if (isChecked) {
                    mUserItems.add(position)

                } else {
                    mUserItems.remove(Integer.valueOf(position))
                }
            })

        mBuilder.setCancelable(false)
        mBuilder.setPositiveButton("Ok") { dialogInterface, which ->
            listOfDays.clear()
            for (i in mUserItems.indices) {
                listOfDays.add(mUserItems[i] + 1)
            }
            Log.i("-----", listOfDays.toString())
        }
        mBuilder.setNegativeButton(
            "Dismiss"
        ) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        mBuilder.setNeutralButton("Clear All") { dialogInterface, which ->
            for (i in checkedItems.indices) {
                checkedItems[i] = false
                mUserItems.clear()
            }
        }
        val mDialog = mBuilder.create()
        mDialog.show()
        ColorOfDialogButton.setColorOfDialogButton(mDialog)
    }

    /**
     * this function ensures that user entered values for all editable fields
     */
    fun validate(): Boolean {
        if (TextUtils.isEmpty(fromTimeEditText.text.trim())) {
            Toast.makeText(applicationContext, "Invalid From Time", Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(toTimeEditText.text.trim())) {
            Toast.makeText(applicationContext, "Invalid To Time", Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(dateFromEditText.text.trim())) {
            Toast.makeText(applicationContext, "Invalid From Date", Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(dateToEditText.text.trim())) {
            Toast.makeText(applicationContext, "Invalid To Date", Toast.LENGTH_SHORT).show()
            return false
        } else if (listOfDays.isEmpty()) {
            Toast.makeText(applicationContext, "Please select week days", Toast.LENGTH_SHORT).show()
            return false
        } else {
            return true
        }
    }

    /**
     * if MIN_MILIISECONDS <= elapsed that means the meeting duration is more than 15 min
     *  if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
     *  if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
     *  if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
     *  if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
     */
    fun applyValidationOnDateAndTime() {
        val min_milliseconds: Long = 900000
        val max_milliseconds: Long = 14400000

        /**
         * Get the start and end time of meeting from the input fields
         */

        val startTime = fromTimeEditText.text.toString()
        val endTime = toTimeEditText.text.toString()

        /**
         * setting a aalert dialog instance for the current context
         */

        val builder = android.app.AlertDialog.Builder(this@ProjectManagerInputActivity)
        builder.setTitle("Check...")
        try {

            /**
             *  getting the values for time validation variables from method calculateTimeInMillis
             */
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMiliis(
                startTime,
                endTime,
                dateFromEditText.text.toString()
            )
            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */

            if (elapsed2 < 0) {
                builder.setMessage("From-Time must be greater than the current time...")
                builder.setPositiveButton("Ok") { dialog, which ->
                }
                val dialog: android.app.AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            } else if ((min_milliseconds <= elapsed) && (max_milliseconds >= elapsed)) {
                goToBuildingsActivity()
            } else {
                val builder = android.app.AlertDialog.Builder(this@ProjectManagerInputActivity)
                builder.setTitle("Check...")
                builder.setMessage("From-Time must be greater then To-Time and the meeting time must be less then 4 Hours")
                builder.setPositiveButton("Ok") { dialog, which ->
                }
                val dialog: android.app.AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@ProjectManagerInputActivity, "Details are Invalid!!!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * set data to the object which is used to send data from this activity to another activity and pass the intent
     */
    fun goToBuildingsActivity() {
        var mSetIntentData = GetIntentDataFromActvity()
        mSetIntentData.fromtime = fromTimeEditText.text.toString().trim()
        mSetIntentData.totime = toTimeEditText.text.toString().trim()
        mSetIntentData.date = dateFromEditText.text.toString().trim()
        mSetIntentData.toDate = dateToEditText.text.toString().trim()
        mSetIntentData.listOfDays.clear()
        mSetIntentData.listOfDays.addAll(listOfDays!!)

        val buildingintent = Intent(this@ProjectManagerInputActivity, ManagerBuildingsActivity::class.java)
        buildingintent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
        startActivity(buildingintent)
    }
}
