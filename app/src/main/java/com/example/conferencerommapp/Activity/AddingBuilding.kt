package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html.fromHtml
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ShowToast
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ValidateField.ValidateInputFields
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel
import es.dmoral.toasty.Toasty

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
        observeData()
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
    fun init(){
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)

    }
    /**
     * observing data for adding Building
     */
    private fun observeData(){
        mAddBuildingViewModel.returnSuccessForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.building_added), Toast.LENGTH_SHORT, true).show()
            finish()

        })
        mAddBuildingViewModel.returnFailureForAddBuilding().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
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
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!ValidateInputFields.validateInputForEmpty(buildingNameEditText.text.toString())) {
            Toast.makeText(this, getString(R.string.missing_building_name), Toast.LENGTH_SHORT).show()
            return false
        } else if (!ValidateInputFields.validateInputForEmpty(buildingPlaceEditText.text.toString())) {
            Toast.makeText(this, getString(R.string.missing_building_place), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * function calls the ViewModel of addingBuilding and send data to the backend
     */
    fun addBuild(mBuilding: AddBuilding) {

        /**
         * Get the progress dialog from GetProgress Helper class
         */
        progressDialog.show()
        mAddBuildingViewModel.addBuildingDetails(mBuilding)

    }
}
