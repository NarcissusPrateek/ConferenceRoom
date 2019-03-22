package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.AddBuilding
import com.example.conferencerommapp.R
import com.example.conferencerommapp.Repository.AddBuildingRepository
import com.example.conferencerommapp.ViewModel.AddBuildingViewModel
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.absoluteValue

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
        mAddBuilding.BName = buildingnameEditText.text.toString().trim()
        mAddBuilding.Place = buildingplaceEditText.text.toString().trim()
    }

    //Validate the Inputs
    fun validateInputs(): Boolean {
        if (buildingnameEditText.text.toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building Name", Toast.LENGTH_SHORT).show()
            return false
        } else if (buildingplaceEditText.text.trim().isEmpty()) {
            Toast.makeText(this, "Enter the Building Place", Toast.LENGTH_SHORT).show()
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
