package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import kotlinx.android.synthetic.main.activity_building_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


public class BuildingsActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>"))
    }
    override fun onResume() {
        super.onResume()
        loadBuildings()
    }
    fun loadBuildings() {

        progressDialog = ProgressDialog(this@BuildingsActivity)
        progressDialog!!.setMessage("Loading....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()

        val bundle: Bundle? = intent.extras
        val from = bundle!!.get(UserInputActivity.EXTRA_FROM_TIME).toString()
        val to = bundle.get(UserInputActivity.EXTRA_TO_TIME).toString()
        val date = bundle.get(UserInputActivity.EXTRA_Date).toString()
        val capacity = bundle.get(UserInputActivity.EXTRA_CAPACITY).toString()
        val DateFromTime = date + " " + from
        val DateToTime = date + " " + to

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
                    building_recycler_view.adapter = BuildingAdapter(buildingList!!,
                        object : BuildingAdapter.BtnClickListener {
                            override fun onBtnClick(buildingId: String?, buildingname: String?) {
                                val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
                                intent.putExtra(UserInputActivity.EXTRA_BUILDING_ID, buildingId)
                                intent.putExtra(UserInputActivity.EXTRA_FROM_TIME, DateFromTime)
                                intent.putExtra(UserInputActivity.EXTRA_TO_TIME, DateToTime)
                                intent.putExtra(UserInputActivity.EXTRA_Date, date)
                                intent.putExtra(UserInputActivity.EXTRA_CAPACITY, capacity)
                                intent.putExtra(UserInputActivity.EXTRA_BUILDING_NAME, buildingname)
                                startActivity(intent)
                            }

                        })
                } else {
                    Toast.makeText(applicationContext, "Unable to Load Buildings", Toast.LENGTH_LONG).show()
                }

            }

        })
    }
}