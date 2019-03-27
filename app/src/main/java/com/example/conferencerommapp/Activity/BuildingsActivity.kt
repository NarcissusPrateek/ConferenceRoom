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
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel

class BuildingsActivity : AppCompatActivity() {

    /**
     * Some late initializer variables for storing the instances of different classes
     */
    private lateinit var customAdapter: BuildingAdapter
    private lateinit  var mBuildingsViewModel: BuildingViewModel
    @BindView(R.id.building_recycler_view) lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>"))
        getViewModel()

    }

    /**
     * get the data from intent
     */
    fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    fun getViewModel() {
        var mIntentDataFromActivity = getIntentData()
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mBuildingsViewModel.getBuildingList(this).observe(this, Observer {
            /**
             * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
             */
            customAdapter = BuildingAdapter(this,
                it!!,
                object : BuildingAdapter.BtnClickListener {
                    override fun onBtnClick(buildingId: String?, buildingName: String?) {
                        mIntentDataFromActivity.buildingId = buildingId
                        mIntentDataFromActivity.buildingName = buildingName
                        goToConferenceRoomActivity(mIntentDataFromActivity)
                    }
                }
            )
            mRecyclerView.adapter = customAdapter
        })
    }

    /**
     * onRestart  of activity we make the api call to referesh the data
     */
    override fun onRestart() {
        super.onRestart()
        mBuildingsViewModel.mBuildingsRepository!!.makeApiCall(this)
    }

    /**
     * pass the intent with data for the ConferenceRoomActivity
     */
    fun goToConferenceRoomActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
        startActivity(intent)
    }
}