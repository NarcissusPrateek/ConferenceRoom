package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_user_inputs.*


@Suppress("DEPRECATION")
class UserInputActivity : AppCompatActivity() {


    @BindView(R.id.date)
    lateinit var dateEditText: EditText
    @BindView(R.id.fromTime)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.spinner2)
    lateinit var capacitySpinner: MaterialSpinner
    lateinit var capacity: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_inputs)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title =
            fromHtml("<font font-size = \"23px\" color=\"#FFFFFF\">" + getString(R.string.Booking_Details) + "</font>")

        setPickerToEditText()
    }

    /**
     * function will invoke whenever the button is hit
     */
    @OnClick(R.id.next)
    fun submit() {
        validationOnDataEnteredByUser()
    }

    /**
     * function will attach date and time picker to the input fields
     */
    private fun setPickerToEditText() {

        /**
         * set Time picker for the edittext fromtime
         */
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }

        /**
         * set Time picker for the edittext toTime
         */
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }
        /**
         * set Date picker for the edittext dateEditText
         */
        dateEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateEditText)
        }
        setCapacitySpinner()
    }

    /**
     * a spinner for selecting room capacity
     */
    private fun setCapacitySpinner() {

        /**
         * array of Integers for setting values into the spinner
         */
        val options = arrayOf(2, 4, 6, 8, 10, 12, 14, 16)

        capacitySpinner.adapter = ArrayAdapter<Int>(this, android.R.layout.simple_list_item_1, options)
        capacitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                capacity = "2"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                capacity = spinner2.getItemAtPosition(position).toString()
            }
        }
    }

    private fun validateFromTime(): Boolean {
        var input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            from_time_layout.error = "Field can't be empty"
            false
        } else {
            from_time_layout.error = null
            from_time_layout.isErrorEnabled = false
            true
        }
    }

    private fun validateToTime(): Boolean {
        var input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            to_time_layout.error = "Field can't be empty"
            false
        } else {
            to_time_layout.error = null
            to_time_layout.isErrorEnabled = false
            true
        }
    }

    private fun validateDate(): Boolean {
        var input = dateEditText.text.toString().trim()
        return if (input.isEmpty()) {
            date_layout.error = "Field can't be empty"
            false
        } else {
            date_layout.error = null
            date_layout.isErrorEnabled = false
            true
        }
    }

    private fun validateSpinner(): Boolean {
        return if (capacity == "Select Capacity") {
            spinner_layout.error = "Field can't be empty"
            false
        } else {
            spinner_layout.error = null
            spinner_layout.isErrorEnabled = false
            true
        }
    }

    /**
     * check validation for all input fields
     */
    private fun validate(): Boolean {

        if (!validateFromTime() or !validateToTime() or !validateDate() or !validateSpinner()) {
            return false
        }
        if (capacity == getString(R.string.select_capacity)) {
            Toast.makeText(applicationContext, getString(R.string.invalid_capacity), Toast.LENGTH_SHORT).show()
            return false
        }
        return true

    }


    /**
     * function will apply some validation on data entered by user
     */
    private fun validationOnDataEnteredByUser() {

        /**
         * Validate each input field whether they are empty or not
         * If the field contains no values we show a toast to user saying that the value is invalid for particular field
         */
        if (validate()) {
            validateTime(fromTimeEditText.text.toString(), toTimeEditText.text.toString())
        }
    }

    /**
     * function will pass the intent with data to the next activity
     */
    private fun goToBuildingsActivity() {
        val mGetIntentDataFromActivity = GetIntentDataFromActvity()
        mGetIntentDataFromActivity.fromTime =
            (dateEditText.text.toString() + " " + fromTimeEditText.text.toString()).trim()
        mGetIntentDataFromActivity.toTime =
            (dateEditText.text.toString() + " " + toTimeEditText.text.toString()).trim()
        mGetIntentDataFromActivity.date = dateEditText.text.toString()
        mGetIntentDataFromActivity.capacity = capacity

        val mBuildingIntent = Intent(this@UserInputActivity, BuildingsActivity::class.java)
        mBuildingIntent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActivity)
        startActivity(mBuildingIntent)
    }

    private fun validateTime(startTime: String, endTime: String) {
        val minMilliseconds: Long = 900000
        val maxMilliseconds: Long = 14400000

        /**
         * setting a aalert dialog instance for the current context
         */
        try {

            /**
             * getting the values for time validation variables from method calculateTimeInMillis
             */
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                date.text.toString()
            )

            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */
            if (elapsed2 < 0) {
                val builder = GetAleretDialog.getDialog(
                    this,
                    getString(R.string.invalid),
                    getString(R.string.invalid_fromtime)
                )
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                GetAleretDialog.showDialog(builder)
            }

            /**
             * if MIN_MILLISECONDS <= elapsed that means the meeting duration is more than 15 min
             * if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
             * if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
             * if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
             * if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
             */
            else if ((minMilliseconds <= elapsed) && (maxMilliseconds >= elapsed)) {
                goToBuildingsActivity()
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
            Toast.makeText(this@UserInputActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG).show()
        }
    }
}

