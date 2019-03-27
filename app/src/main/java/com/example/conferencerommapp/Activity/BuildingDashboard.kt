package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.ConferenceDashBoard
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.github.clans.fab.FloatingActionButton

class BuildingDashboard : AppCompatActivity() {

    lateinit var mBuildingsViewModel: BuildingViewModel
    lateinit var addBuilding : FloatingActionButton
    lateinit var buildingAdapter: BuildingAdapter
    lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_dashboard)
        val actionBar = supportActionBar

        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Building_Dashboard) + "</font>"))

        addBuilding = findViewById(R.id.add_building)
        addBuilding.setOnClickListener {
            startActivity(Intent(this, AddingBuilding::class.java))
        }
        recyclerView = findViewById(R.id.buidingRecyclerView)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        getViewModel()
    }
    override fun onRestart() {
        super.onRestart()
        mBuildingsViewModel.mBuildingsRepository!!.makeApiCall(this)
    }
    private fun getViewModel() {
        mBuildingsViewModel.getBuildingList(this).observe(this, Observer {

            /**
             * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
             */
            buildingAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingName: String?) {
                        var intent = Intent(this@BuildingDashboard, ConferenceDashBoard::class.java)
                        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                        startActivity(intent)
                    }
                }
            )
            recyclerView.adapter = buildingAdapter
        })
    }
}