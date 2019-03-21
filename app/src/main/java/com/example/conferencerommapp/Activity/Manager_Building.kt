package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Toast
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import kotlinx.android.synthetic.main.activity_building_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Manager_Building : AppCompatActivity() {

    var datalist = ArrayList<String>()
    var from_List = ArrayList<String>()
    var to_list = ArrayList<String>()
    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__building)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>"))
    }

    override fun onResume() {
        super.onResume()
        loadBuildings()
    }

    private fun loadBuildings() {
        progressDialog = ProgressDialog(this@Manager_Building)
        progressDialog!!.setMessage("Loading....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()


        val bundle: Bundle? = intent.extras
        val from = bundle!!.get(Constants.EXTRA_FROM_TIME).toString()
        val to = bundle.get(Constants.EXTRA_TO_TIME).toString()
        val from_Date = bundle.get(Constants.EXTRA_DATE).toString()
        val to_Date = bundle.get(Constants.EXTRA_TO_DATE).toString()
        val listOfDays = bundle.getIntegerArrayList(Constants.EXTRA_DAY_LIST)

        getDateAccordingToDay(from, to, from_Date, to_Date, listOfDays)
        Log.i("---------", from_List.toString())
        Log.i("---------", to_list.toString())
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Building>> = conferenceService.getBuildingList()
        requestCall.enqueue(object : Callback<List<Building>> {
            override fun onFailure(call: Call<List<Building>>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Building>>, response: Response<List<Building>>) {
                if (response.isSuccessful) {
                    progressDialog!!.dismiss()
                    val buildingList: List<Building>? = response.body()
                    Log.i("-----------", buildingList.toString())
//                    building_recycler_view.adapter = BuildingAdapter(buildingList!!,
//                        object : BuildingAdapter.BtnClickListener {
//                            override fun onBtnClick(buildingId: String?, buildingname: String?) {
//                                val intent = Intent(this@Manager_Building, Manager_Conference_Room::class.java)
//                                intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
//                                intent.putExtra(Constants.EXTRA_FROM_TIME_LIST, from_List)
//                                intent.putExtra(Constants.EXTRA_TO_TIME_LIST, to_list)
//                                intent.putExtra(Constants.EXTRA_DATE, from_Date)
//                                intent.putExtra(Constants.EXTRA_TO_DATE, to_Date)
//                                intent.putExtra(Constants.EXTRA_BUILDING_NAME, buildingname)
//                                startActivity(intent)
//                            }
//
//                        })
                } else {
                    Toast.makeText(applicationContext, "Unable to Load Buildings", Toast.LENGTH_LONG).show()
                }

            }

        })
    }

    fun getDateAccordingToDay(
        start: String,
        end: String,
        fromDate: String,
        toDate: String,
        listOfDays: ArrayList<Int>
    ) {
        datalist.clear()
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d1 = simpleDateFormat.parse(fromDate)
            val d2 = simpleDateFormat.parse(toDate)
            val c1 = Calendar.getInstance()
            val c2 = Calendar.getInstance()
            c1.setTime(d1)
            c2.setTime(d2)
            var sundays = 0

            while (c2.after(c1)) {
                if (listOfDays.contains(c1.get(Calendar.DAY_OF_WEEK))) {
                    datalist.add(simpleDateFormat.format(c1.time).toString())
                }
                c1.add(Calendar.DATE, 1)
            }
            getLists(start, end)
            Log.i("--------", datalist.toString())
            Toast.makeText(this@Manager_Building, sundays.toString(), Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.i("--------", e.message)
        }
    }

    fun getLists(start: String, end: String) {
        for (item in datalist) {
            from_List.add(item + " ${start}")
            to_list.add(item + " ${end}")
        }
    }
}


