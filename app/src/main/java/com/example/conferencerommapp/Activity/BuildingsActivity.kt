package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
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
    lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
        init()
        observerData()
        getViewModel()

    }

    /**
     * get the data from intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * observe data from server
     */
    private fun observerData() {
        val mIntentDataFromActivity = getIntentData()
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            /**
             * different cases for different result from api call
             * if response is empty than show Toast
             * if response is ok than we can set data into the adapter
             */
            if (it.isEmpty()) {
                Toast.makeText(this, "No Building Available", Toast.LENGTH_SHORT).show()
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

        })
        // Negative response from server
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            finish()
        })
    }

    /**
     * get the object of ViewModel using ViewModelProviders and observers the data from backend
     */
    private fun getViewModel() {
        progressDialog.show()
        // make api call
        mBuildingsViewModel.getBuildingList()
    }

    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
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