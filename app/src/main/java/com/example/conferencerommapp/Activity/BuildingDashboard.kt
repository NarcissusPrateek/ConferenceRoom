@file:Suppress("DEPRECATION")

package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel

class BuildingDashboard : AppCompatActivity() {
    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.buidingRecyclerView)
    lateinit var recyclerView: RecyclerView
    lateinit var buildingAdapter: BuildingAdapter
    lateinit var mBuildingsViewModel: BuildingViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_dashboard)

        val actionBar = supportActionBar
        actionBar!!.title =
            Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Building_Dashboard) + "</font>")

        ButterKnife.bind(this)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        getViewModel()
    }

    /**
     * onClick on this button goes to AddBuilding Activity
     */
    @OnClick(R.id.button_add_building)
    fun addBuildingFloatingButton() {
        startActivity(Intent(this, AddingBuilding::class.java))
    }

    /**
     * Restart the Activity
     */

    override fun onRestart() {
        super.onRestart()
        mBuildingsViewModel.mBuildingsRepository!!.makeApiCall(this)
    }

    /**
     * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
     */
    private fun getViewModel() {
        mBuildingsViewModel.getBuildingList(this).observe(this, Observer {
            buildingAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingname: String?) {
                        val intent = Intent(this@BuildingDashboard, ConferenceDashBoard::class.java)
                        intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                        startActivity(intent)
                    }
                }
            )
            recyclerView.adapter = buildingAdapter
        })
    }
}