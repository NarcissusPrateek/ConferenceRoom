package com.example.conferencerommapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Helper.ConvertTimeInMillis
import com.example.conferencerommapp.Helper.DateAndTimePicker
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.BlockingConfirmation
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.ViewModel.BlockConferenceRoomListViewModel
import com.example.conferencerommapp.ViewModel.BlockConfirmationViewModel
import com.example.conferencerommapp.ViewModel.BlockRoomViewModel
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_spinner.*
import kotlinx.android.synthetic.main.activity_user_inputs.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.DatabaseMetaData
import java.text.SimpleDateFormat
import java.util.*

class Spinner : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null
    lateinit var blockButton: Button
    var room = BlockRoom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Block) + "</font>"))

        blockButton = findViewById(R.id.block_conference)
        setDialogsToInputFields()

        getBuilding()//setting spinner
        blockButton.setOnClickListener {
            if(validateInput()){
                validationOnDataEnteredByUser()
            }
        }
    }

    fun addDataToobject() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        room.Email = acct!!.email
        room.Purpose = Purpose.text.toString()
        room.FromTime = date_block.text.toString() +  " " + fromTime_b.text.toString()
        room.ToTime = date_block.text.toString() +  " " + toTime_b.text.toString()
        room.Status = "abc"
    }

    fun setDialogsToInputFields() {

        fromTime_b.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTime_b)
        }

        toTime_b.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTime_b)
        }

        date_block.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, date_block)
        }


    }

    private fun getBuilding() {
        var mBuildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mBuildingViewModel.getBuildingList(this).observe(this, androidx.lifecycle.Observer {
            buildingListFromBackend(it)
        })
    }

    private fun blocking(room: BlockRoom) {
        var mBlockConfirmationViewModel = ViewModelProviders.of(this).get(BlockConfirmationViewModel::class.java)
        mBlockConfirmationViewModel.blockingStatus(this, room)!!.observe(this, androidx.lifecycle.Observer {
            Log.i("@@@",it.toString())
            if (it.mStatus == 0) {
                Log.i("@@@","BlockConfirmation")
                blockConfirmed(room)
            } else {
                val builder = AlertDialog.Builder(this@Spinner)
                builder.setTitle("Blocking Status")
                var name = it.Name
                var purpose = it.Purpose
                builder.setMessage(
                    "Room is already Booked by Employee ${name} for ${purpose}.\nAre you sure the 'BLOCKING' is Necessary?"
                )
                builder.setPositiveButton("Ok") { dialog, which ->
                    blockConfirmed(room)
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        })
    }

    fun blockConfirmed(room : BlockRoom) {
        var mBlockRoomViewModel = ViewModelProviders.of(this).get(BlockRoomViewModel::class.java)
        mBlockRoomViewModel.blockRoom(this, room).observe(this, androidx.lifecycle.Observer {
            val builder =
                AlertDialog.Builder(this@Spinner)
            builder.setTitle("Blocking Status")
            builder.setMessage("Room is  Blocked...")
            builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                finish()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        })
    }

    fun buildingListFromBackend(it: List<Building>) {
        it?.let {
            sendDataForSpinner(it)
        }
    }

    fun sendDataForSpinner(it: List<Building>) {
        var items = mutableListOf<String>()
        var items_id = mutableListOf<Int>()
        for (item in it) {
            items.add(item.buildingName!!)
            items_id.add(item.buildingId!!.toInt())
        }
        buiding_Spinner.adapter = ArrayAdapter<String>(this@Spinner, android.R.layout.simple_list_item_1, items)
        buiding_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.BId = items_id[position]
                ConferenceRoomListFromBackend(items_id[position])
            }
        }
    }

    fun ConferenceRoomListFromBackend(buildingId: Int) {
        var mBlockConferenceRoomListViewModel = ViewModelProviders.of(this).get(BlockConferenceRoomListViewModel::class.java)
        mBlockConferenceRoomListViewModel.getRoomList(this, buildingId)!!.observe(this, androidx.lifecycle.Observer {
            setSpinnerToConferenceList(it)
        })
    }

    fun setSpinnerToConferenceList(it: List<BuildingConference>) {
        var conference_name = mutableListOf<String>()
        var conference_id = mutableListOf<Int>()

        for (item in it) {
            conference_name.add(item.CName!!)
            conference_id.add(item.CId)
        }
        conference_Spinner.adapter = ArrayAdapter<String>(this@Spinner, android.R.layout.simple_list_item_1, conference_name)
        conference_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long){
                    room.CId = conference_id[position]
                }
        }

    }

    fun validateInput(): Boolean {
        if (TextUtils.isEmpty(fromTime_b.text.trim())) {
            Toast.makeText(
                applicationContext,
                "Please enter the From-Time",
                Toast.LENGTH_SHORT
            ).show()
            return false

        } else if (TextUtils.isEmpty(toTime_b.text.trim())) {
            Toast.makeText(
                applicationContext,
                "Please enter the To-Time",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (TextUtils.isEmpty(date_block.text.trim())) {
            Toast.makeText(
                applicationContext,
                "Please enter the Date",
                Toast.LENGTH_SHORT
            ).show()
            return false
        } else if (Purpose.text.toString().trim().isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please enter the purpose of Meeting",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }else {
            return true
        }
    }

    fun validationOnDataEnteredByUser() {
            val min_milliseconds: Long = 900000
            val max_milliseconds: Long = 14400000

            val startTime = fromTime_b.text.toString()
            val endTime = toTime_b.text.toString()

            val builder = AlertDialog.Builder(this@Spinner)
            builder.setTitle("Check...")
            try {
                val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMiliis(
                    startTime,
                    endTime,
                    date_block.text.toString()
                )
                if (elapsed2 < 0) {
                    builder.setMessage("From-Time must be greater than the current time...")
                    builder.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
                else if ((min_milliseconds <= elapsed) && (max_milliseconds >= elapsed)) {
                    Log.i("---------","I m here")
                    blockRoom()
                }
                else {
                    val builder = AlertDialog.Builder(this@Spinner)
                    builder.setTitle("Check...")
                    builder.setMessage("From-Time must be greater than To-Time and the meeting time must be less than 4 Hours")
                    builder.setPositiveButton("Ok") { dialog, which ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            } catch (e: Exception) {
                Log.i("------------", e.message)
                Toast.makeText(this@Spinner, getString(R.string.details_invalid), Toast.LENGTH_LONG).show()
            }
        }

    fun blockRoom() {
        addDataToobject()
        blocking(room)
    }
}
