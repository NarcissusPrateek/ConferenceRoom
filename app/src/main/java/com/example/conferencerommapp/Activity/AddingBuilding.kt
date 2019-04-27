package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_building.*
import kotlinx.android.synthetic.main.activity_spinner.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("DEPRECATION")
class AddingBuilding : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.edit_text_building_name)
    lateinit var buildingNameEditText: EditText
    @BindView(R.id.edit_text_building_place)
    lateinit var buildingPlaceEditText: EditText

    private lateinit var mAddBuildingViewModel: AddBuildingViewModel
    private var mAddBuilding = AddBuilding()
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_building)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Buildings) + "</font>")
        ButterKnife.bind(this)
        init()
        textChangeListenerOnBuildingName()
        textChangeListenerOnBuildingPlace()
        observeData()
    }

    /**
     * add text change listener for the building Name
     */
    private fun textChangeListenerOnBuildingName() {
        buildingNameEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateBuildingName()
            }
        })
    }

    /**
     * add text change listener for the building place
     */
    private fun textChangeListenerOnBuildingPlace() {
        buildingPlaceEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateBuildingPlace()
            }
        })
    }


    /**
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.button_add_building)
    fun getBuildingDetails() {
        if (validateInputs()) {
            addDataToObject(mAddBuilding)
            addBuild(mAddBuilding)
        }
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
    }

    /**
     * observing data for adding Building
     */
    private fun observeData() {
        mAddBuildingViewModel.returnSuccessForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.building_added), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddBuildingViewModel.returnFailureForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
            }
        })
    }
    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mAddBuilding: AddBuilding) {
        mAddBuilding.buildingName = buildingNameEditText.text.toString().trim()
        mAddBuilding.place = buildingPlaceEditText.text.toString().trim()
    }
    /**
     * validation for building employeeList
     */
    private fun validateBuildingName(): Boolean {
        val input = buildingNameEditText.text.toString().trim()
        return if (input.isEmpty()) {
            building_name_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            building_name_layout.error = null
            true
        }
    }
    /**
     * validation for building place
     */
    private fun validateBuildingPlace(): Boolean {
        val input = buildingPlaceEditText.text.toString().trim()
        return if (input.isEmpty()) {
            location_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            location_layout.error = null
            true
        }
    }
    /**
     * validate the purpose using regrex
     */

    private fun validateBuildingNameRegrex():Boolean{

        val purposePattern: String = "^[A-Za-z]+"
        val pattern: Pattern = Pattern.compile(purposePattern)
        val matcher: Matcher = pattern.matcher(buildingNameEditText.text)
        //        return matcher.matches()
        return if(!matcher.matches()){
            building_name_layout.error = getString(R.string.invalid_purpose_name)
            false
        }
        else{
            true
        }
    }
    private fun validateBuildingPlaceRegrex():Boolean{

        val purposePattern: String = "^[A-Za-z]+"
        val pattern: Pattern = Pattern.compile(purposePattern)
        val matcher: Matcher = pattern.matcher(buildingPlaceEditText.text)
        //        return matcher.matches()
        return if(!matcher.matches()){
            location_layout.error = getString(R.string.invalid_purpose_name)
            false
        }
        else{
            true
        }
    }
    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!validateBuildingName() or !validateBuildingPlace() or !validateBuildingNameRegrex() or !validateBuildingPlaceRegrex()) {
            return false
        }
        return true
    }
    /**
     * function calls the ViewModel of addingBuilding and send data to the backend
     */
    private fun addBuild(mBuilding: AddBuilding) {

        /**
         * Get the progress dialog from GetProgress Helper class
         */
        progressDialog.show()
        mAddBuildingViewModel.addBuildingDetails(mBuilding, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(
            this, getString(R.string.session_expired), "Your session is expired!\n" +
                    getString(R.string.session_expired_messgae)
        )
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
    fun getTokenFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.token), getString(R.string.not_set))!!
    }

    fun getUserIdFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.user_id), getString(R.string.not_set))!!
    }

}
