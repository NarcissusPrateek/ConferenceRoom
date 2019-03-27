package com.example.conferencerommapp.Activity

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R

class ProjectManagerInputActivity : AppCompatActivity() {

    @BindView(R.id.fromTime_manager) private lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime_manager) private lateinit var toTimeEditText: EditText
    @BindView(R.id.to_date_manager) private lateinit var dateToEditText: EditText
    @BindView(R.id.date_manager) private lateinit var dateFromEditText: EditText
    private var listOfDays = ArrayList<Int>()
    private var mUserItems = ArrayList<Int>()
    private var listItems = resources.getStringArray(R.array.days)
    private var checkedItems: BooleanArray = BooleanArray(listItems.size)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Booking Details" + "</font>"))

        setPickerToEdittextx()

    }


    /**
     * on Button click
     */
    @OnClick(R.id.next_manager, R.id.select_days)
    fun onViewClicked(view: View) {
        when(view.id) {
            R.id.next_manager -> {
                if (validate()) {
                    applyValidationOnDateAndTime()
                }
            }
            R.id.select_days -> {
                getDays()
            }
        }
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
        mBuilder.setPositiveButton(getString(R.string.ok)) { dialogInterface, which ->
            listOfDays.clear()
            for (i in mUserItems.indices) {
                listOfDays.add(mUserItems[i] + 1)
            }
        }
        mBuilder.setNegativeButton(
            getString(R.string.dismiss_label)
        ) { dialogInterface, i ->
            dialogInterface.dismiss()
        }
        mBuilder.setNeutralButton(getString(R.string.clear_all)) { dialogInterface, which ->
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
            Toast.makeText(applicationContext, getString(R.string.invalid_from_time), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(toTimeEditText.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_to_time), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(dateFromEditText.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_from_date), Toast.LENGTH_SHORT).show()
            return false
        } else if (TextUtils.isEmpty(dateToEditText.text.trim())) {
            Toast.makeText(applicationContext, getString(R.string.invalid_to_date), Toast.LENGTH_SHORT).show()
            return false
        } else if (listOfDays.isEmpty()) {
            Toast.makeText(applicationContext, getString(R.string.select_days), Toast.LENGTH_SHORT).show()
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
                var builder = GetAleretDialog.getDialog(this, getString(R.string.invalid),getString(R.string.invalid_fromtime))
                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                }
                GetAleretDialog.showDialog(builder)
            } else if ((min_milliseconds <= elapsed) && (max_milliseconds >= elapsed)) {
                if(ConvertTimeInMillis.calculateDateinMillis(dateFromEditText.text.toString(), dateToEditText.text.toString())) {
                    goToBuildingsActivity()
                }else {
                    Toast.makeText(this, getString(R.string.invalid_fromDate_or_toDate), Toast.LENGTH_SHORT).show()
                }

            } else {
                val builder = GetAleretDialog.getDialog(this, getString(R.string.invalid), getString(R.string.time_validation_message))

                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
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
