package com.nineleaps.buttonchecker


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.conferencerommapp.R
import kotlinx.android.synthetic.main.prateek_building.*
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*





public class Buildings__Activity__new : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prateek_building)
        val items = listOf(1,2,3,4,5,6,7,8,9,10)
        val cal = Calendar.getInstance()

        cal.timeInMillis

//        val starttime = "22:19"
//        try {
//           val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
//            val d1 = sdf.parse("23/02/2019 ${starttime}")
//           // Log.i("------d1-------", d1.time.toString())
//            //Log.i("------current-------", cal.timeInMillis.toString())
//            val elapsed = d1.getTime() - cal.timeInMillis
//            Log.i("------ttyt-------", elapsed.toString())
//        } catch (e: Exception) {
//           Log.i("-------------", e.toString())
//        }


      building_recycler_view.adapter = Building__Adapter__new(items, this@Buildings__Activity__new)
    }

}