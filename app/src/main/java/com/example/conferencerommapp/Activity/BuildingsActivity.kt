package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Html.fromHtml
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import es.dmoral.toasty.Toasty

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
                Toasty.info(this,  getString(R.string.empty_building_list), Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
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
            }

        })
        // Negative response from server
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
               // setRefereshedToken()
               // Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show()
            } else {
                ShowToast.show(this, it)
                finish()
            }
        })
    }
    /**
     * show dialog for session expired!
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
     * sign out logic
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
     * get the object of ViewModel using ViewModelProviders and observers the data from backend
     */
    private fun getViewModel() {
        progressDialog.show()
        // make api call
        mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
    }

    fun init() {
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
    }

    /**
     * onRestart  of activity we make the api call to refresh the data
     */
    override fun onRestart() {
        super.onRestart()
        mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * pass the intent with data for the ConferenceRoomActivity
     */
    fun goToConferenceRoomActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
        val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
        startActivity(intent)
    }

    /**
     * get token and userId from local storage
     */
    fun getTokenFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("Token", "Not Set")!!
    }

    fun getUserIdFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("UserId", "Not Set")!!
    }

    private fun setRefereshedToken() {
        val token = GoogleSignIn.getLastSignedInAccount(this)!!.idToken
        Log.i("----------refreshed", "" + token)
        val editor = getSharedPreferences("myPref", Context.MODE_PRIVATE).edit()
        editor.putString("Token", token)
        editor.apply()
    }
}