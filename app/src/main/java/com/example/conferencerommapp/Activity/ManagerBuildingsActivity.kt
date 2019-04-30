package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.ManagerBuildingViewModel
import es.dmoral.toasty.Toasty

@Suppress("DEPRECATION")
class ManagerBuildingsActivity : AppCompatActivity() {

    private lateinit var mManagerBuildingViewModel: ManagerBuildingViewModel
    private lateinit var mCustomAdapter: BuildingAdapter
    @BindView(R.id.building_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mIntentDataFromActivity: GetIntentDataFromActvity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager__building)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
        init()
        //loadBuildings()
    }

    /**
     * on restart of activity the function will make a call to ViewModel method that will get the updated data from backend
     */
    override fun onRestart() {
        super.onRestart()
        mManagerBuildingViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
    }





    private fun observerData() {
        mManagerBuildingViewModel.returnBuildingSuccess().observe(this, androidx.lifecycle.Observer {
            progressDialog.dismiss()
            if (it.isEmpty()) {
                Toasty.info(this, getString(R.string.empty_building_list), Toast.LENGTH_SHORT, true).show()
            } else {
                mCustomAdapter = BuildingAdapter(this,
                    it!!,
                    object : BuildingAdapter.BtnClickListener {
                        override fun onBtnClick(buildingId: String?, buildingName: String?) {
                            mIntentDataFromActivity.buildingId = buildingId
                            mIntentDataFromActivity.buildingName = buildingName
                            goToNextActivity(mIntentDataFromActivity)
                        }
                    }
                )
                mRecyclerView.adapter = mCustomAdapter
            }
        })
        mManagerBuildingViewModel.returnBuildingFailure().observe(this, androidx.lifecycle.Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
                finish()
            }
        })
    }

    /**
     * set the observer on a method of viewmodel getBuildingList which will observe the data from the api
     * after that whenever data changes it will set a adapter to recyclerview
     */
    private fun getViewModel() {
        progressDialog.show()
        mManagerBuildingViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())

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

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }
    /**
     * get token and userId from local storage
     */
    private fun getTokenFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("Token", "Not Set")!!
    }

    private fun getUserIdFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("UserId", "Not Set")!!
    }
}


