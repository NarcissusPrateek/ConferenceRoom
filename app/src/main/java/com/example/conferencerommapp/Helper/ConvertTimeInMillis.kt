package com.example.conferencerommapp.Helper

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

class ConvertTimeInMillis {
    /**
     * it will provide a static function
     */
    companion object {

        /**
         * convert the time into milliseconds and return the difference between two time
         */
        @SuppressLint("SimpleDateFormat")
        fun calculateTimeInMiliis(startTime: String, endTime: String, date: String): Pair<Long, Long> {
            val simpleDateFormatForTime = SimpleDateFormat("HH:mm")
            val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
            val d1 = simpleDateFormatForTime.parse(startTime)
            val d2 = simpleDateFormatForTime.parse(endTime)
            val d3 = simpleDateFormatForDate.parse("$date $startTime")

            val elapsed = d2.time - d1.time
            val currtime = System.currentTimeMillis()
            val elapsed2 = d3.time - currtime
            return Pair(elapsed, elapsed2)
        }
        @SuppressLint("SimpleDateFormat")
        fun calculateDateinMillis(fromDate: String, toDate: String): Boolean {
            val simpleDateFormatForDate1 = SimpleDateFormat("yyyy-M-dd")
            val fromDateinDateFormat = simpleDateFormatForDate1.parse(fromDate)
            val toDateinDateFormat = simpleDateFormatForDate1.parse(toDate)
            val dateDifference = toDateinDateFormat.time - fromDateinDateFormat.time
            if(dateDifference > 0) {
                return true
            }
            return false
        }
    }
}