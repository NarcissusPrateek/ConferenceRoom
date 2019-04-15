package com.example.conferencerommapp.Activity

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ValidateField.ValidateInputFields
import com.example.conferencerommapp.ViewModel.AddConferenceRoomViewModel
import fr.ganfra.materialspinner.MaterialSpinner

@Suppress("DEPRECATION")
class AddingConference : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    var capacity = ""
    @BindView(R.id.conference_Name)
    lateinit var conferenceRoomEditText: EditText

    @BindView(R.id.conference_Capacity)
    lateinit var capacitySpinner: MaterialSpinner

    private lateinit var mAddConferenceRoomViewModel: AddConferenceRoomViewModel
    private var mConferenceRoom = AddConferenceRoom()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_conference)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Room) + "</font>")
        setSpinnerForCapacity()

    }


    /**
     * function will invoke whenever the add button is clicked
     */
    @OnClick(R.id.add_conference_room)
    fun addRoomButton() {
        if (validateInputs()) {
            addDataToObject(mConferenceRoom)
            addRoom(mConferenceRoom)
        }
    }

    /**
     * fuction will set the BlockConferenceRoomActivity Value for the capacity
     */
    fun setSpinnerForCapacity() {
        val capacitySpinnerOptions = arrayOf(2, 4, 6, 8, 10, 12, 14)
        capacitySpinner.adapter =
            ArrayAdapter<Int>(this@AddingConference, android.R.layout.simple_list_item_1, capacitySpinnerOptions)
        capacitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                capacity = "2"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                capacity = capacitySpinner.getItemAtPosition(position).toString()
            }

        }
    }

    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject(mConferenceRoom: AddConferenceRoom) {
        val bundle: Bundle? = intent.extras
        val buildingId = bundle!!.get(Constants.EXTRA_BUILDING_ID)!!.toString().toInt()
        mConferenceRoom.bId = buildingId
        mConferenceRoom.roomName = conferenceRoomEditText.text.toString().trim()
        mConferenceRoom.capacity = capacity.toInt()


    }

    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!ValidateInputFields.validateInputForEmpty(conferenceRoomEditText.text.toString().trim())) {
            Toast.makeText(this@AddingConference, getString(R.string.enter_room_name), Toast.LENGTH_LONG).show()
            return false
        } else if (capacity == getString(R.string.select_capacity)) {
            Toast.makeText(this@AddingConference, getString(R.string.select_capacity), Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    /**
     * function calls the ViewModel of addingConference and data into the database
     */
    private fun addRoom(mConferenceRoom: AddConferenceRoom) {
        val mProgressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
        mProgressDialog.show()
        mAddConferenceRoomViewModel.addConferenceDetails(mConferenceRoom)
        mAddConferenceRoomViewModel.returnSuccessForAddingRoom().observe(this, Observer {
            mProgressDialog.dismiss()
            val dialog
                    =
                GetAleretDialog.getDialog(this, getString(R.string.status), getString(R.string.room_added))
            dialog.setPositiveButton(getString(R.string.ok)) { _, _ ->
                finish()
            }
            GetAleretDialog.showDialog(dialog)
        })
        mAddConferenceRoomViewModel.returnFailureForAddingRoom().observe(this, Observer {
            mProgressDialog.dismiss()
            // some message
        })
    }
}
