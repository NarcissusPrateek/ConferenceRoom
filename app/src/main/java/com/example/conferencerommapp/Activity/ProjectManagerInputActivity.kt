package com.example.conferencerommapp.Activity

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.R
import kotlinx.android.synthetic.main.activity_project_manager_input.*
import kotlinx.android.synthetic.main.activity_user_inputs.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProjectManagerInputActivity : AppCompatActivity() {

    var datalist = ArrayList<String>()
    var listOfDays = ArrayList<Int>()
    var mUserItems = ArrayList<Int>()
    var listItems = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thusday", "Friday", "Saturday")
    var checkedItems: BooleanArray = BooleanArray(listItems.size)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_manager_input)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + "Booking Details" + "</font>"))

        var timeFormat = SimpleDateFormat("HH:mm ", Locale.US)
        var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        fromTime_manager.setOnClickListener {
            val now = Calendar.getInstance()
            val timePickerDialog =
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    var nowtime = timeFormat.format(selectedTime.time).toString()
                    fromTime_manager.text = Editable.Factory.getInstance().newEditable(nowtime)
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }



        toTime_manager.setOnClickListener {
            val now = Calendar.getInstance()
            val timePickerDialog =
                TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    var nowtime = timeFormat.format(selectedTime.time).toString()
                    toTime_manager.text = Editable.Factory.getInstance().newEditable(nowtime)
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }


        date_manager.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker =
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    //
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    var nowDate: String = dateFormat.format(selectedDate.time).toString()
                    date_manager.text = Editable.Factory.getInstance().newEditable(nowDate)
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show()

        }
        to_date_manager.setOnClickListener {
            val now = Calendar.getInstance()
            val datePicker =
                DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    //
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    var nowDate: String = dateFormat.format(selectedDate.time).toString()
                    to_date_manager.text = Editable.Factory.getInstance().newEditable(nowDate)
                    Log.i("--------",Editable.Factory.getInstance().newEditable(nowDate).toString())
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show()
        }

        select_days.setOnClickListener(View.OnClickListener {
            getDays()

        })


        next_manager.setOnClickListener {
            if (TextUtils.isEmpty(fromTime_manager.text.trim())) {
                Toast.makeText(applicationContext, "Invalid From Time", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(toTime_manager.text.trim())) {
                Toast.makeText(applicationContext, "Invalid To Time", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(date_manager.text.trim())) {
                Toast.makeText(applicationContext, "Invalid From-Date", Toast.LENGTH_SHORT).show()
            }
            else if (TextUtils.isEmpty(to_date_manager.text.trim())) {
                Toast.makeText(applicationContext, "Invalid To-Date", Toast.LENGTH_SHORT).show()
            }else if (listOfDays.isEmpty()) {
                Toast.makeText(applicationContext, "Please select week days", Toast.LENGTH_SHORT).show()
            }
            else {
                val startTime = fromTime_manager.text.toString()
                val endTime = toTime_manager.text.toString()
                try {
                    val sdf = SimpleDateFormat("HH:mm", Locale.US)
                    val sdf1 = SimpleDateFormat("yyyy-M-dd HH:mm", Locale.US)
                    val d1 = sdf.parse(startTime)
                    val d2 = sdf.parse(endTime)
                    val d3 = sdf1.parse(date_manager.text.toString() + " " + fromTime_manager.text.toString())
                    val elapsed = d2.time - d1.time
                    var min: Long = 900000
                    val currtime = System.currentTimeMillis()
                    val elapsed2 = d3.time - currtime
                    Log.i("-----time---",d3.time.toString() + d3.toString())
                    Log.i("-----current---",currtime.toString())
                    Log.i("--------diff------", " " + elapsed2 )
                    var max: Long = 14400000
                    if(elapsed2 < 0) {
                        val builder = android.app.AlertDialog.Builder(this@ProjectManagerInputActivity)
                        builder.setTitle("Check...")
                        builder.setMessage("From-Time must be greater than the current time...")
                        builder.setPositiveButton("Ok") { dialog, which ->
                        }
                        val dialog: android.app.AlertDialog = builder.create()
                        dialog.setCanceledOnTouchOutside(false)
                        dialog.show()
                    }
                    else if ((min <= elapsed) && (max >= elapsed)) {
                        val buildingintent = Intent(this@ProjectManagerInputActivity, Manager_Building::class.java)
                        buildingintent.putExtra(Constants.EXTRA_FROM_TIME, fromTime_manager.text.toString())
                        buildingintent.putExtra(Constants.EXTRA_TO_TIME, toTime_manager.text.toString())
                        buildingintent.putExtra(Constants.EXTRA_DATE, date_manager.text.toString())
                        buildingintent.putExtra(Constants.EXTRA_TO_DATE, to_date_manager.text.toString())
                        buildingintent.putExtra(Constants.EXTRA_DAY_LIST, listOfDays)
                        startActivity(buildingintent)
                    }
                    else {
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
                    Log.i("---------", e.message)
                    Toast.makeText(this@ProjectManagerInputActivity, "Details are Invalid!!!", Toast.LENGTH_LONG).show()
                }

            } }
    }
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
        var buttonPositive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        var buttonNegative = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        var buttonNutral = mDialog.getButton(AlertDialog.BUTTON_NEUTRAL)

        buttonPositive.setBackgroundColor(Color.WHITE)
        buttonPositive.setTextColor(Color.parseColor("#0072BC"))

        buttonNegative.setBackgroundColor(Color.WHITE)
        buttonNegative.setTextColor(Color.parseColor("#0072BC"))

        buttonNutral.setBackgroundColor(Color.WHITE)
        buttonNutral.setTextColor(Color.RED)
    }

}