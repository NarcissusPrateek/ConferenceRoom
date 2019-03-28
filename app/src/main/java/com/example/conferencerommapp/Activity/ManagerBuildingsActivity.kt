package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ManagerBuildingViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManagerBuildingsActivity : AppCompatActivity() {

    private var dataList = ArrayList<String>()
    private var fromTimeList = ArrayList<String>()
    private var toTimeList = ArrayList<String>()
    private lateinit var mManagerBuildingViewModel: ManagerBuildingViewModel
    private lateinit var mCustomAdapter: BuildingAdapter
    @BindView(R.id.building_recycler_view)
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__building)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>"))
        loadBuildings()
    }

    /**
     * on restart of activity the function will make a call to viewmodel method that will get the updated data from backend
     */
    override fun onRestart() {
        super.onRestart()
        mManagerBuildingViewModel.getBuildingList(this)
    }

    private fun loadBuildings() {
        var mGetIntentDataFromActvity = getIntentData()
        getDateAccordingToDay(
            mGetIntentDataFromActvity.fromtime!!,
            mGetIntentDataFromActvity.totime!!,
            mGetIntentDataFromActvity.date!!,
            mGetIntentDataFromActvity.toDate!!,
            mGetIntentDataFromActvity.listOfDays
        )
        getViewModel(mGetIntentDataFromActvity)
    }

    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get all date for each day selected by user in between from date and To date
     */
    fun getDateAccordingToDay(
        start: String,
        end: String,
        fromDate: String,
        toDate: String,
        listOfDays: ArrayList<Int>
    ) {
        dataList.clear()
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d1 = simpleDateFormat.parse(fromDate)
            val d2 = simpleDateFormat.parse(toDate)
            val c1 = Calendar.getInstance()
            val c2 = Calendar.getInstance()
            c1.setTime(d1)
            c2.setTime(d2)
            while (c2.after(c1)) {
                if (listOfDays.contains(c1.get(Calendar.DAY_OF_WEEK))) {
                    dataList.add(simpleDateFormat.format(c1.time).toString())
                }
                c1.add(Calendar.DATE, 1)
            }
            getLists(start, end)
        } catch (e: Exception) {
            Toast.makeText(this@ManagerBuildingsActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * this function returns all fromdate list and todate list
     */
    fun getLists(start: String, end: String) {
        for (item in dataList) {
            fromTimeList.add(item + " ${start}")
            toTimeList.add(item + " ${end}")
        }
    }

    /**
     * set the observer on a method of viewmodel getBuildingList which will observe the data from the api
     * after that whenever data changes it will set a adapter to recyclerview
     */
    fun getViewModel(mIntentDataFromActivity: GetIntentDataFromActvity) {
        mManagerBuildingViewModel = ViewModelProviders.of(this).get(ManagerBuildingViewModel::class.java)
        mManagerBuildingViewModel.getBuildingList(this).observe(this, androidx.lifecycle.Observer {
            mCustomAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingName: String?) {
                        mIntentDataFromActivity.buildingId = buildingId
                        mIntentDataFromActivity.buildingName = buildingName
                        mIntentDataFromActivity.fromTimeList.clear()
                        mIntentDataFromActivity.toTimeList.clear()
                        mIntentDataFromActivity.fromTimeList.addAll(fromTimeList)
                        mIntentDataFromActivity.toTimeList.addAll(toTimeList)
                        goToNextActivity(mIntentDataFromActivity)
                    }
                }
            )
            mRecyclerView.adapter = mCustomAdapter
        })
    }

    /**
     * intent to the ManagerConferenceRoomActivity
     */
    fun goToNextActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val mIntent = Intent(this@ManagerBuildingsActivity, ManagerConferenceRoomActivity::class.java)
        mIntent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
        startActivity(mIntent)

    }
}


