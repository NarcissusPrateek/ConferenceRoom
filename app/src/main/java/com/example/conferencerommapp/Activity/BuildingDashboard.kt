@file:Suppress("DEPRECATION")

package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BuildingViewModel

class BuildingDashboard : AppCompatActivity() {
    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.buidingRecyclerView)
    lateinit var recyclerView: RecyclerView
    private lateinit var buildingAdapter: BuildingAdapter
    private lateinit var mBuildingsViewModel: BuildingViewModel
    private lateinit var mProgressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_dashboard)

        val actionBar = supportActionBar
        actionBar!!.title =
            Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Building_Dashboard) + "</font>")

        ButterKnife.bind(this)
        init()
        observeData()
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
        mBuildingsViewModel.getBuildingList()
    }

    /**
     * initialize objects
     */
    private fun init() {
        mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            mProgressDialog.dismiss()
            when(it){
                null->{
                    Toast.makeText(this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    if(!it.isEmpty()) {
                        buildingAdapter = BuildingAdapter(this,it,object : BuildingAdapter.BtnClickListener {
                            override fun onBtnClick(buildingId: String?, buildingname: String?) {
                                val intent = Intent(this@BuildingDashboard, ConferenceDashBoard::class.java)
                                intent.putExtra(Constants.EXTRA_BUILDING_ID, buildingId)
                                startActivity(intent)
                            }
                        })
                        recyclerView.adapter = buildingAdapter
                    }
                }
            }
        })
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            mProgressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
                finish()
            }
            //some message goes here
        })
    }

    /**
     * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
     */
     private fun getViewModel() {
        mProgressDialog.show()
        // making API call
        mBuildingsViewModel.getBuildingList()
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        var dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        var builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        var mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }
}