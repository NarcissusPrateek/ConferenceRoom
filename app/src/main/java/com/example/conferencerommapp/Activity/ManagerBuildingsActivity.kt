package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ShowToast
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.ManagerBuildingViewModel
import es.dmoral.toasty.Toasty
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION")
class ManagerBuildingsActivity : AppCompatActivity() {

    private var dataList = ArrayList<String>()
    private var fromTimeList = ArrayList<String>()
    private var toTimeList = ArrayList<String>()
    private lateinit var mManagerBuildingViewModel: ManagerBuildingViewModel
    private lateinit var mCustomAdapter: BuildingAdapter
    @BindView(R.id.building_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__building)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
        init()
        loadBuildings()
    }

    /**
     * on restart of activity the function will make a call to ViewModel method that will get the updated data from backend
     */
    override fun onRestart() {
        super.onRestart()
        mManagerBuildingViewModel.getBuildingList()
    }

    private fun loadBuildings() {
        val mGetIntentDataFromActvity = getIntentData()
        getDateAccordingToDay(
            mGetIntentDataFromActvity.fromTime!!,
            mGetIntentDataFromActvity.toTime!!,
            mGetIntentDataFromActvity.date!!,
            mGetIntentDataFromActvity.toDate!!,
            mGetIntentDataFromActvity.listOfDays
        )
        getViewModel(mGetIntentDataFromActvity)
    }

    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get all date for each day selected by user in between from date and To date
     */
    private fun getDateAccordingToDay(
        start: String,
        end: String,
        fromDate: String,
        toDate: String,
        listOfDays: ArrayList<String>
    ) {
        dataList.clear()
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val d1 = simpleDateFormat.parse(fromDate)
            val d2 = simpleDateFormat.parse(toDate)
            val c1 = Calendar.getInstance()
            val c2 = Calendar.getInstance()
            c1.time = d1
            c2.time = d2
            while (c2.after(c1)) {
                if (listOfDays.contains(
                        c1.getDisplayName(
                            Calendar.DAY_OF_WEEK,
                            Calendar.LONG_FORMAT,
                            Locale.US
                        ).toUpperCase()
                    )
                ) {
                    dataList.add(simpleDateFormat.format(c1.time).toString())
                }
                c1.add(Calendar.DATE, 1)
            }
            if (listOfDays.contains(
                    c2.getDisplayName(
                        Calendar.DAY_OF_WEEK,
                        Calendar.LONG_FORMAT,
                        Locale.US
                    ).toUpperCase()
                )
            ) {
                dataList.add(simpleDateFormat.format(c1.time).toString())
            }
            getLists(start, end)
        } catch (e: Exception) {
            Toast.makeText(this@ManagerBuildingsActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    /**
     * this function returns all fromdate list and todate list
     */
    private fun getLists(start: String, end: String) {
        for (item in dataList) {
            fromTimeList.add("$item $start")
            toTimeList.add("$item $end")
        }
    }

    /**
     * set the observer on a method of viewmodel getBuildingList which will observe the data from the api
     * after that whenever data changes it will set a adapter to recyclerview
     */
    private fun getViewModel(mIntentDataFromActivity: GetIntentDataFromActvity) {
        progressDialog.show()
        mManagerBuildingViewModel.getBuildingList()
        mManagerBuildingViewModel.returnBuildingSuccess().observe(this, androidx.lifecycle.Observer {
            progressDialog.dismiss()
            if (it.isEmpty()) {
                Toasty.info(this, getString(R.string.empty_building_list), Toast.LENGTH_SHORT, true).show()
            } else {
                mCustomAdapter = BuildingAdapter(this,
                    it!!,
                    object : BuildingAdapter.BtnClickListener {
                        override fun onBtnClick(buildingId: String?, buildingname: String?) {
                            mIntentDataFromActivity.buildingId = buildingId
                            mIntentDataFromActivity.buildingName = buildingname
                            mIntentDataFromActivity.fromTimeList.clear()
                            mIntentDataFromActivity.toTimeList.clear()
                            mIntentDataFromActivity.fromTimeList.addAll(fromTimeList)
                            mIntentDataFromActivity.toTimeList.addAll(toTimeList)
                            goToNextActivity(mIntentDataFromActivity)
                        }
                    }
                )
                mRecyclerView.adapter = mCustomAdapter
            }
        })
        mManagerBuildingViewModel.returnBuildingFailure().observe(this, androidx.lifecycle.Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
            finish()
        })
    }

    /**
     * initialize lateinit variables
     */
    fun init() {
        mManagerBuildingViewModel = ViewModelProviders.of(this).get(ManagerBuildingViewModel::class.java)
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)

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


