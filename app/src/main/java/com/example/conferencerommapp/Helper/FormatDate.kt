package com.example.conferencerommapp.Helper

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class FormatDate {
    companion object {
        val simpleDateFormatInput = SimpleDateFormat("yyyy-MM-dd")
        val simpleDateFormatOutput = SimpleDateFormat("dd MMMM yyyy")

        /**
         * format date from yyyy-MM-dd to dd-MM-yyyy
         */
        fun formatDate(date: String): String {
            return simpleDateFormatOutput.format(simpleDateFormatInput.parse(date))
        }

        /**
         * format date from dd-MM-yyyy to yyyy-MM-dd
         */
        fun reverseDateFormat(date: String): String {
            return simpleDateFormatInput.format(simpleDateFormatOutput.parse(date))
        }
    }
}