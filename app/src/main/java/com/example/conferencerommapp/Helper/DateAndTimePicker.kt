package com.example.conferencerommapp.Helper

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.text.Editable
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

class DateAndTimePicker {
    companion object {
        fun getTimePickerDialog(context: Context, setTime: EditText) {
            var timeFormat = SimpleDateFormat("HH:mm ", Locale.US)
            val now = Calendar.getInstance()
            val timePickerDialog =
                TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val selectedTime = Calendar.getInstance()
                    selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    selectedTime.set(Calendar.MINUTE, minute)

                    var nowtime = timeFormat.format(selectedTime.time).toString()
                    setTime.text = Editable.Factory.getInstance().newEditable(nowtime)
                }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true)
            timePickerDialog.show()
        }
        fun getDatePickerDialog(context: Context, setDate: EditText) {
            var dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val now = Calendar.getInstance()
            val datePicker =
                DatePickerDialog(context, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    var nowDate: String = dateFormat.format(selectedDate.time).toString()
                    setDate.text = Editable.Factory.getInstance().newEditable(nowDate)
                }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
            datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePicker.show()
        }
    }
}