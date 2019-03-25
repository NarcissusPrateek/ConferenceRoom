package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel

class AddingBuilding : AppCompatActivity() {

    //Initializing the variable
    var progressDialog: ProgressDialog? = null
    lateinit var buildingnameEditText : EditText
    lateinit var buildingplaceEditText : EditText
    lateinit var addbuildingButton : Button
    lateinit var mAddBuildingViewModel: AddBuildingViewModel
    var mAddBuilding = AddBuilding()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_building)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Buildings) + "</font>"))

        addbuildingButton = findViewById(R.id.addbuilding)

        addbuildingButton.setOnClickListener {
            getBuildingDetails()
            if(validateInputs())
               addBuild(mAddBuilding)
        }
    }

    //Get the data from the Fields
    fun getBuildingDetails(){
        buildingnameEditText = findViewById(R.id.input_buildingName)
        buildingplaceEditText = findViewById(R.id.input_buildingPlace)
        addDataToObject(mAddBuilding)
    }

    //Add the Data to the Objects
    private fun addDataToObject(mAddBuilding: AddBuilding) {
        mAddBuilding.buildingName = buildingnameEditText.text.toString().trim()
        mAddBuilding.place = buildingplaceEditText.text.toString().trim()
    }

    //Validate the Inputs
    fun validateInputs(): Boolean {
        if (buildingnameEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building name", Toast.LENGTH_SHORT).show()
            return false
        } else if (buildingplaceEditText.text.trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building place", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Calling the ViewModel
    private fun addBuild(building: AddBuilding) {
        mAddBuildingViewModel = ViewModelProviders.of(this).get(AddBuildingViewModel::class.java)
        mAddBuildingViewModel.addBuildingDetails(this,building)!!.observe(this, Observer{
            if (it == 200){
                    //finish()
            }

        })
    }
}
