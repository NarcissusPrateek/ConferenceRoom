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
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel

@Suppress("DEPRECATION")
class AddingBuilding : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.input_buildingName)
    lateinit var buildingNameEditText: EditText
    @BindView(R.id.input_buildingPlace)
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
    @OnClick(R.id.add_building)
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
        if (buildingNameEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building name", Toast.LENGTH_SHORT).show()
            return false
        } else if (buildingPlaceEditText.text.trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building place", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * function calls the ViewModel of addingBuilding and data into the database
     */
    private fun addBuild(building: AddBuilding) {
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
        mAddBuildingViewModel.addBuildingDetails(this, building)!!.observe(this, Observer {
            if (it == Constants.OK_RESPONSE) {
                val dialog = GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.room_added))
                dialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    finish()
                }
                GetAleretDialog.showDialog(dialog)
            }

        })
    }
}
