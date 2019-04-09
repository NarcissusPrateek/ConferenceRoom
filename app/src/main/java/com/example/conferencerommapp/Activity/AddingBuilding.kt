package com.example.conferencerommapp.Activity

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
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ValidateField.ValidateInputFields
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel

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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_building)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Buildings) + "</font>")
        ButterKnife.bind(this)
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
    private fun addBuild(building: AddBuilding) {
        /**
         * Get the progress dialog from GetProgress Helper class
         */
        val mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
        mProgressDialog.show()
        mAddBuildingViewModel.addBuildingDetails(building)!!.observe(this, Observer {
            mProgressDialog.dismiss()
            when (it) {
                Constants.OK_RESPONSE -> {
                    val dialog =
                        GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.room_added))
                    dialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
                        finish()
                    }
                    GetAleretDialog.showDialog(dialog)
                }
                Constants.INTERNAL_SERVER_ERROR -> {
                    Toast.makeText(this, getString(R.string.internal_server_error), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    val dialog = GetAleretDialog.getDialog(this, getString(R.string.status), "Bla Bla Bla")
                    GetAleretDialog.showDialog(dialog)
                }
            }
        })
    }
}
