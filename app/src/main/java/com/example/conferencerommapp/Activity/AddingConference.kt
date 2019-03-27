package com.example.conferencerommapp

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.ViewModel.AddConferenceRoomViewModel
import kotlinx.android.synthetic.main.activity_adding_conference.*

class AddingConference : AppCompatActivity() {

    //Initializing the variable
    var capacitySpinnerOptions = arrayOf(2, 4, 6, 8, 10, 12, 14)
    var capacity = ""
    var progressDialog: ProgressDialog? = null
    lateinit var conferenceRoomEditText : EditText
    lateinit var addConferenceRoomButton : Button
    lateinit var mAddConferenceRoomViewModel : AddConferenceRoomViewModel
    var mConferenceRoom = AddConferenceRoom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_conference)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Room) + "</font>"))
        addConferenceRoomButton = findViewById(R.id.add_conference_room)
        conference_Capacity.adapter = ArrayAdapter<Int>(this@AddingConference, android.R.layout.simple_list_item_1, capacitySpinnerOptions)
        conference_Capacity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                capacity = "2"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                capacity = conference_Capacity.getItemAtPosition(position).toString()
            }

        }
        addConferenceRoomButton.setOnClickListener {
            initializeInputFields()
            if (validateInputs()){
                addDataToObject(mConferenceRoom)
                addRoom(mConferenceRoom)
            }
        }
    }


    private fun initializeInputFields() {
        conferenceRoomEditText = findViewById(R.id.conference_Name)
    }

    private fun addDataToObject(mConferenceRoom : AddConferenceRoom) {
        val bundle: Bundle?= intent.extras
        val buildingId = bundle!!.get("BuildingId").toString().toInt()
        mConferenceRoom.BId = buildingId
        mConferenceRoom.CName = conferenceRoomEditText.text.toString().trim()
        mConferenceRoom.Capacity = capacity.toInt()
    }

    fun validateInputs(): Boolean {
        if(conferenceRoomEditText.text.toString().trim().isEmpty()){
            Toast.makeText(this@AddingConference, "Please Enter Room name", Toast.LENGTH_LONG).show()
            return false
        }
        else if(capacity.equals("Please Select Capacity")){
            Toast.makeText(this@AddingConference, "Please Select Capacity", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun addRoom(mConferenceRoom: AddConferenceRoom) {
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
        mAddConferenceRoomViewModel.addConferenceDetails(this,mConferenceRoom).observe(this, Observer{

        })
    }

}

