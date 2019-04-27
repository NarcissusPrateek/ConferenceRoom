package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
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
import java.text.SimpleDateFormat
import java.util.*

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
    private var dataList = ArrayList<String>()
    private var fromTimeList = ArrayList<String>()
    private var toTimeList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + "Booking Details" + "</font>")
        init()

    }

    private fun init() {
        setPickerToEditTexts()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnFromDateEditText()
        textChangeListenerOnToDateEditText()
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

    /**
     * validate from time for non empty condition
     */
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

    /**
     * validate to-time for non empty condition
     */
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

    /**
     * validate to-date for non empty condition
     */
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

    /**
     * validate from-date for non empty condition
     */
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

    /**
     * validate day selector for non empty condition
     */
    private fun validateSelectedDayList(): Boolean {
        if (day_picker.selectedDays.isEmpty()) {
            error_day_selector_text_view.visibility = View.VISIBLE
            return false
        }
        error_day_selector_text_view.visibility = View.GONE
        return true
    }


    /**
     * this function ensures that user entered values for all editable fields
     */
    private fun validate(): Boolean {

        if (!validateFromTime() or !validateToTime() or !validateFromDate() or !validateToDate() or !validateSelectedDayList()) {
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
        getDateAccordingToDay(
            fromTimeEditText.text.toString(),
            toTimeEditText.text.toString(),
            dateFromEditText.text.toString(),
            dateToEditText.text.toString(),
            listOfDays
        )
        mSetIntentData.fromTimeList.clear()
        mSetIntentData.toTimeList.clear()
        mSetIntentData.fromTimeList.addAll(fromTimeList)
        mSetIntentData.toTimeList.addAll(toTimeList)

        if (dataList.isEmpty()) {
            Toast.makeText(this, "No dates founds for selected days!", Toast.LENGTH_SHORT).show()
        } else {
            val buildingIntent = Intent(this@ProjectManagerInputActivity, ManagerBuildingsActivity::class.java)
            buildingIntent.putExtra(Constants.EXTRA_INTENT_DATA, mSetIntentData)
            startActivity(buildingIntent)
        }
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


    /**
     * get all date for each day selected by user in between from date and To date
     */
    private fun getDateAccordingToDay(
        start: String,
        end: String,
        fromDate: String,
        toDate: String,
        listOfDays: ArrayList<String>
    ) {
        dataList.clear()
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d1 = simpleDateFormat.parse(fromDate)
            val d2 = simpleDateFormat.parse(toDate)
            val c1 = Calendar.getInstance()
            val c2 = Calendar.getInstance()
            c1.time = d1
            c2.time = d2
            while (c2.after(c1)) {
                if (listOfDays.contains(
                        c1.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.LONG_FORMAT,
                            Locale.US
                        ).toUpperCase()
                    )
                ) {
                    dataList.add(simpleDateFormat.format(c1.time).toString())
                }
                c1.add(Calendar.DATE, 1)
            }
            if (listOfDays.contains(
                    c2.getDisplayName(
                        Calendar.DAY_OF_WEEK,
                        Calendar.LONG_FORMAT,
                        Locale.US
                    ).toUpperCase()
                )
            ) {
                dataList.add(simpleDateFormat.format(c1.time).toString())
            }
            getLists(start, end)
        } catch (e: Exception) {
            Toast.makeText(this@ProjectManagerInputActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * this function returns all fromdate list and todate list
     */
    private fun getLists(start: String, end: String) {
        for (item in dataList) {
            fromTimeList.add("$item $start")
            toTimeList.add("$item $end")
        }
    }

    /**
     * add text change listener for the start time edit text
     */
    private fun textChangeListenerOnFromTimeEditText() {
        fromTimeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFromTime()
            }
        })
    }
    /**
     * add text change listener for the end time edit text
     */
    private fun textChangeListenerOnToTimeEditText() {
        toTimeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateToTime()
            }
        })
    }
    /**
     * add text change listener for the start edit text
     */
    private fun textChangeListenerOnFromDateEditText() {
        dateFromEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateToDate()

            }
        })
    }
    /**
     * add text change listener for the end date edit text
     */
    private fun textChangeListenerOnToDateEditText() {
        dateToEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFromDate()
            }
        })
    }
}
