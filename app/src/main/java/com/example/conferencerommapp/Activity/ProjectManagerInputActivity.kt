package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.ConvertTimeInMillis
import com.example.conferencerommapp.Helper.DateAndTimePicker
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import kotlinx.android.synthetic.main.activity_project_manager_input.*
import kotlinx.android.synthetic.main.activity_user_inputs.*

@Suppress("NAME_SHADOWING", "DEPRECATION")
class ProjectManagerInputActivity : AppCompatActivity() {

    @BindView(R.id.fromTime_manager)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime_manager)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.to_date_manager)
    lateinit var dateToEditText: EditText
    @BindView(R.id.date_manager)
    lateinit var dateFromEditText: EditText
    private var listOfDays = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + "Booking Details" + "</font>")
        setPickerToEditTexts()
    }




    /**
     * on Button click
     */
    @OnClick(R.id.next_manager)
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.next_manager -> {
                if (validate()) {
                    applyValidationOnDateAndTime()
                }
            }
        }
    }


    /**
     * set date and time pickers to edittext fields
     */
    private fun setPickerToEditTexts() {

        /**
         * set Time picker for the editText fromTimeEditText
         */
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }
        /**
         * set Time picker for the EditText toTimeEditText
         */
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }
        /**
         * set Date picker for the EditText dateEditText
         */
        dateFromEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateFromEditText)
        }
        /**
         * set Date picker for the EditText dateToEditText
         */
        dateToEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateToEditText)
        }
    }

    private fun validateFromTime(): Boolean {
        var input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_from_time_layout.error = null
            true
        }
    }

    private fun validateToTime(): Boolean {
        var input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_to_time_layout.error = null
            true
        }
    }

    private fun validateToDate(): Boolean {
        var input = dateFromEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_from_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_from_date_layout.error = null
            true
        }
    }

    private fun validateFromDate(): Boolean {
        var input = dateToEditText.text.toString().trim()
        return if (input.isEmpty()) {
            manager_to_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            manager_to_date_layout.error = null
            true
        }
    }

    private fun validateSelectedDayList(): Boolean {
        if(day_picker.selectedDays.isEmpty()) {
            Toast.makeText(this, getString(R.string.select_days), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }



    /**
     * this function ensures that user entered values for all editable fields
     */
    private fun validate(): Boolean {

        if(!validateFromTime() or !validateToTime() or !validateFromDate() or !validateToDate() or !validateSelectedDayList()) {
            return false
        }
        return true
    }

    /**
     * if MIN_MILIISECONDS <= elapsed that means the meeting duration is more than 15 min
     *  if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
     *  if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
     *  if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
     *  if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
     */
    private fun applyValidationOnDateAndTime() {
        val minMilliseconds: Long = 900000
        val maxMilliseconds: Long = 14400000

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
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                dateFromEditText.text.toString()
            )
            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */

            if (elapsed2 < 0) {
                val builder =
                    GetAleretDialog.getDialog(this, getString(R.string.invalid), getString(R.string.invalid_fromtime))
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                GetAleretDialog.showDialog(builder)
            } else if ((minMilliseconds <= elapsed) && (maxMilliseconds >= elapsed)) {
                if (ConvertTimeInMillis.calculateDateInMilliseconds(
                        dateFromEditText.text.toString(),
                        dateToEditText.text.toString()
                    )
                ) {
                    goToBuildingsActivity()
                } else {
                    Toast.makeText(this, getString(R.string.invalid_fromDate_or_toDate), Toast.LENGTH_SHORT).show()
                }

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
            Toast.makeText(this@ProjectManagerInputActivity, "Details are Invalid!!!", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * set data to the object which is used to send data from this activity to another activity and pass the intent
     */
    private fun goToBuildingsActivity() {
        val mSetIntentData = GetIntentDataFromActvity()
        mSetIntentData.fromTime = fromTimeEditText.text.toString().trim()
        mSetIntentData.toTime = toTimeEditText.text.toString().trim()
        mSetIntentData.date = dateFromEditText.text.toString().trim()
        mSetIntentData.toDate = dateToEditText.text.toString().trim()
        mSetIntentData.listOfDays.clear()
        getListOfSelectedDays()
        mSetIntentData.listOfDays.addAll(listOfDays)
        val buildingIntent = Intent(this@ProjectManagerInputActivity, ManagerBuildingsActivity::class.java)
        buildingIntent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
        startActivity(buildingIntent)
    }

    /**
     * get all the selected days and add all days to another list listOfDays
     */
    private fun getListOfSelectedDays() {
        listOfDays.clear()
        for (day in day_picker.selectedDays) {
            listOfDays.add("${day}")
        }
    }
}
