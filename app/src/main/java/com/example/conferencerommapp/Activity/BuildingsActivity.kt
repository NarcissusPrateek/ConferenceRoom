package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel

class BuildingsActivity : AppCompatActivity() {

    /*Some late initilizer variable for storing the instances of different classes*/
    lateinit var mBuildingsViewModel: BuildingViewModel
    lateinit var customAdapter: BuildingAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>"))

        // getting the data from intent

        val bundle: Bundle? = intent.extras
        val from = bundle!!.get(Constants.EXTRA_FROM_TIME).toString()
        val to = bundle.get(Constants.EXTRA_TO_TIME).toString()
        val date = bundle.get(Constants.EXTRA_DATE).toString()
        val capacity = bundle.get(Constants.EXTRA_CAPACITY).toString()
        val DateFromTime = date + " " + from
        val DateToTime = date + " " + to


        recyclerView = findViewById(R.id.building_recycler_view)

        // creating the object of BuildingViewModel class
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)

        //setting a observer on getBuildingList() method of BuildingViewModel to observe the data
        mBuildingsViewModel.getBuildingList(this).observe(this, Observer {

            // setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class

            customAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingname: String?) {
                        val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
                        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                        intent.putExtra(Constants.EXTRA_FROM_TIME, DateFromTime)
                        intent.putExtra(Constants.EXTRA_TO_TIME, DateToTime)
                        intent.putExtra(Constants.EXTRA_DATE, date)
                        intent.putExtra(Constants.EXTRA_CAPACITY, capacity)
                        intent.putExtra(Constants.EXTRA_BUILDING_NAME, buildingname)
                        startActivity(intent)
                    }
                }
            )
            recyclerView.adapter = customAdapter
            if ( mBuildingsViewModel.getBuildingList(this).hasActiveObservers()){
                mBuildingsViewModel.getBuildingList(this).removeObservers(this)
            }
        })
    }

    // onRestart  of activity we make the api call to referesh the data
    override fun onRestart() {
        super.onRestart()
        Log.i("---------", "on ReStart is called")
        mBuildingsViewModel.mBuildingsRepository!!.makeApiCall(this)
    }
}