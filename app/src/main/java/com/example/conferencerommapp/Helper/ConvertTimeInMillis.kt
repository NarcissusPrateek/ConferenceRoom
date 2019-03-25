package com.example.conferencerommapp.Helper

import java.text.SimpleDateFormat

class ConvertTimeInMillis {
    /**
     * it will provide a static function
     */
    companion object {

        /**
         * convert the time into milliseconds and return the difference between two time
         */
        fun calculateTimeInMiliis(startTime: String, endTime: String, date: String): Pair<Long, Long> {
            val simpleDateFormatForTime = SimpleDateFormat("HH:mm")
            val simpleDateFormatForDate = SimpleDateFormat("yyyy-M-dd HH:mm")
            val d1 = simpleDateFormatForTime.parse(startTime)
            val d2 = simpleDateFormatForTime.parse(endTime)
            val d3 = simpleDateFormatForDate.parse("${date} ${startTime}")
            val elapsed = d2.time - d1.time
            val currtime = System.currentTimeMillis()
            val elapsed2 = d3.time - currtime
            return Pair(elapsed, elapsed2)
        }
    }
}