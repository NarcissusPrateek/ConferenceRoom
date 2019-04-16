package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ShowToast
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.AddConferenceRoomViewModel
import es.dmoral.toasty.Toasty
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.activity_adding_conference.*

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
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_conference)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Room) + "</font>")
        setSpinnerForCapacity()
        init()
        observeData()

    }
    /**
     * initialize all lateinit variables
     */
    fun init(){
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
    }
    /**
     * observing data for adding conference
     */
    fun observeData(){
        mAddConferenceRoomViewModel.returnSuccessForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_add_success), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })
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
    private fun setSpinnerForCapacity() {
        val capacitySpinnerOptions = arrayOf(2, 4, 6, 8, 10, 12, 14)
        capacitySpinner.adapter =
            ArrayAdapter<Int>(this@AddingConference, android.R.layout.simple_list_item_1, capacitySpinnerOptions) as SpinnerAdapter?
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
     * validation for room name
     */
    private fun validateRoomName(): Boolean {
        val input = conferenceRoomEditText.text.toString().trim()
        return if(input.isEmpty()) {
            room_name_layout_name.error = getString(R.string.field_cant_be_empty)
            false
        }else {
            room_name_layout_name.error = null
            room_name_layout_name.isErrorEnabled = false
            true
        }
    }

    /**
     * validation for spinner
     */
    private fun validateSpinner(): Boolean {
        return if(capacity == getString(R.string.select_capacity)) {
            Toast.makeText(this, getString(R.string.enter_room_name), Toast.LENGTH_SHORT).show()
            false
        }else {
            true
        }
    }

    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if(!validateRoomName() or !validateSpinner()) {
            return false
        }
        return true
    }

    /**
     * function calls the ViewModel of addingConference and data into the database
     */
    private fun addRoom(mConferenceRoom: AddConferenceRoom) {
        progressDialog.show()
        mAddConferenceRoomViewModel.addConferenceDetails(mConferenceRoom)

    }
}
