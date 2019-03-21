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
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
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

        recyclerView = findViewById(R.id.building_recycler_view)

        // getting the data from intent
        var mIntentDataFromActivity: GetIntentDataFromActvity = getIntentData()
        getViewModel(mIntentDataFromActivity)

    }

    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    fun getViewModel(mIntentDataFromActivity: GetIntentDataFromActvity) {
        // creating the object of BuildingViewModel class
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)

        //setting a observer on getBuildingList() method of BuildingViewModel to observe the data
        mBuildingsViewModel.getBuildingList(this).observe(this, Observer {

            // setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class

            customAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingName: String?) {
                        mIntentDataFromActivity.buildingId = buildingId
                        mIntentDataFromActivity.buildingName = buildingName
                        goToNextActivity(mIntentDataFromActivity)
                    }
                }
            )
            recyclerView.adapter = customAdapter
            if (mBuildingsViewModel.getBuildingList(this).hasActiveObservers()) {
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

    fun goToNextActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
        startActivity(intent)
    }
}