package com.example.conferencerommapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel

@Suppress("DEPRECATION")
class BuildingsActivity : AppCompatActivity() {

    /**
     * Some late initializer variables for storing the instances of different classes
     */
    private lateinit var customAdapter: BuildingAdapter
    private lateinit var mBuildingsViewModel: BuildingViewModel
    @BindView(R.id.building_recycler_view)
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
        getViewModel()

    }

    /**
     * get the data from intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * get the object of ViewModel using ViewModelProviders and observers the data from backend
     */
    private fun getViewModel() {

        /**
         * get progress dialog
         */

        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        val mIntentDataFromActivity = getIntentData()
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        progressDialog.show()
        mBuildingsViewModel.getBuildingList()

        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            /**
             * different cases for different result from api call
             * if the response is NULL than some iternal error is there
             * if response is empty than
             * if response is ok than we can set data into the adapter
             */
            when (it) {
                null -> {
                    Toast.makeText(this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show()
                    finish()
                }
                else -> {
                    if (it.isEmpty()) {
                        Toast.makeText(this, "Some messgae from backend", Toast.LENGTH_SHORT).show()
                    } else {
                        /**
                         * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
                         */
                        customAdapter = BuildingAdapter(this,
                            it!!,
                            object : BuildingAdapter.BtnClickListener {
                                override fun onBtnClick(buildingId: String?, buildingname: String?) {
                                    mIntentDataFromActivity.buildingId = buildingId
                                    mIntentDataFromActivity.buildingName = buildingname
                                    goToConferenceRoomActivity(mIntentDataFromActivity)
                                }
                            }
                        )
                        mRecyclerView.adapter = customAdapter
                    }
                }
            }
        })

        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()
            // some message goes here for error code
        })

    }

    /**
     * onRestart  of activity we make the api call to refresh the data
     */
    override fun onRestart() {
        super.onRestart()
        mBuildingsViewModel.getBuildingList()
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