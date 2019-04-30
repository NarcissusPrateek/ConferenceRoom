package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Helper.BuildingAdapter
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.example.conferencerommapp.ViewModel.RefreshTokenViewModel

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
    lateinit var mRefreshTokenViewModel: RefreshTokenViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_building_list)
        ButterKnife.bind(this)
        //     init()
        //    observerData()
        //   getViewModel()
    }
}

//    /**
//     * get the data from intent
//     */
//    private fun getIntentData(): GetIntentDataFromActvity {
//        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
//    }
//
//    /**
//     * observe data from server
//     */
//    private fun observerData() {
//        val mIntentDataFromActivity = getIntentData()
//        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
//            progressDialog.dismiss()
//            /**
//             * different cases for different result from api call
//             * if response is empty than show Toast
//             * if response is ok than we can set data into the adapter
//             */
//            if (it.isEmpty()) {
//                Toasty.info(this,  getString(R.string.empty_building_list), Toast.LENGTH_SHORT, true).show()
//                finish()
//            } else {
//                /**
//                 * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
//                 */
//                customAdapter = BuildingAdapter(this,
//                    it!!,
//                    object : BuildingAdapter.BtnClickListener {
//                        override fun onBtnClick(buildingId: String?, buildingName: String?) {
//                            mIntentDataFromActivity.buildingId = buildingId
//                            mIntentDataFromActivity.buildingName = buildingName
//                            goToConferenceRoomActivity(mIntentDataFromActivity)
//                        }
//                    }
//                )
//                mRecyclerView.adapter = customAdapter
//            }
//
//        })
//        // Negative response from server
//        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
//            if(it == getString(R.string.invalid_token)) {
//                progressDialog.dismiss()
//                showAlert()
//                //mRefreshTokenViewModel.getAccessAndRefreshToken(RefreshToken(getAccessTokenFromPreference(), getRefreshTokenFromPreference()))
//            } else {
//                progressDialog.dismiss()
//                ShowToast.show(this, it)
//                finish()
//            }
//        })
//
//        // positive response for refresh token and access token
//        mRefreshTokenViewModel.returnAccessToken().observe(this, Observer {
//            setAccessAndRefreshToken(it)
//            mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
//        })
//        //negative response for access and refresh token
//        mRefreshTokenViewModel.returnAccessTokenFailure().observe(this, Observer {
//            progressDialog.dismiss()
//            Toasty.error(this, it, Toast.LENGTH_SHORT, true).show()
//            finish()
//        })
//    }
////    /**
////     * show dialog for session expired!
////     */
////    private fun showAlert() {
////
////        val dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
////                getString(R.string.session_expired_messgae))
////        dialog.setPositiveButton(R.string.ok) { _, _ ->
////            signOut()
////        }
////        val builder = GetAleretDialog.showDialog(dialog)
////        ColorOfDialogButton.setColorOfDialogButton(builder)
////    }
////    /**
////     * sign out logic
////     */
////    private fun signOut() {
////        val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
////        mGoogleSignInClient.signOut()
////            .addOnCompleteListener(this) {
////                startActivity(Intent(applicationContext, SignIn::class.java))
////                finish()
////            }
////    }
//
//    /**
//     * get the object of ViewModel using ViewModelProviders and observers the data from backend
//     */
//    private fun getViewModel() {
//        progressDialog.show()
//        // make api call
//        mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
//    }
//
//    fun init() {
//        val actionBar = supportActionBar
//        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Buildings) + "</font>")
//        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
//        mRefreshTokenViewModel = ViewModelProviders.of(this).get(RefreshTokenViewModel::class.java)
//        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
//    }
//
//    /**
//     * onRestart  of activity we make the api call to refresh the data
//     */
//    override fun onRestart() {
//        super.onRestart()
//        mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
//    }
////
////    /**
////     * pass the intent with data for the ConferenceRoomActivity
////     */
////    fun goToConferenceRoomActivity(mIntentDataFromActivity: GetIntentDataFromActvity) {
////        val intent = Intent(this@BuildingsActivity, ConferenceRoomActivity::class.java)
////        intent.putExtra(Constants.EXTRA_INTENT_DATA, mIntentDataFromActivity)
////        startActivity(intent)
////    }
////
////    /**
////     * get token and userId from local storage
////     */
////    fun getTokenFromPreference(): String {
////        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.token), getString(R.string.not_set))!!
////    }
////
////    fun getUserIdFromPreference(): String {
////        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.user_id), getString(R.string.not_set))!!
////    }
////    /**
////     * get access token from local storage
////     */
////    private fun getRefreshTokenFromPreference(): String {
////        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.refresh_token), getString(R.string.not_set))!!
////    }
////
////    /**
////     * get refresh token from local storage
////     */
////    private fun getAccessTokenFromPreference(): String {
////        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.access_token), getString(R.string.not_set))!!
////    }
////
////    private fun setAccessAndRefreshToken(mRefreshToken: RefreshToken) {
////        val editor = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).edit()
////        editor.putString(getString(R.string.token), mRefreshToken.accessToken)
////        editor.putString(getString(R.string.refresh_token), mRefreshToken.refreshToken)
////        editor.apply()
////    }
//}