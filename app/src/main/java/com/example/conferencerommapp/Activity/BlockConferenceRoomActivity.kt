
package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html.fromHtml
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.ConvertTimeInMillis
import com.example.conferencerommapp.Helper.DateAndTimePicker
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.BlockConferenceRoomListViewModel
import com.example.conferencerommapp.ViewModel.BlockConfirmationViewModel
import com.example.conferencerommapp.ViewModel.BlockRoomViewModel
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_spinner.*

@Suppress("NAME_SHADOWING", "DEPRECATION")
class BlockConferenceRoomActivity : AppCompatActivity() {

    @BindView(R.id.fromTime_b)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime_b)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.date_block)
    lateinit var dateEditText: EditText
    @BindView(R.id.Purpose)
    lateinit var purposeEditText: EditText

    var room = BlockRoom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Block) + "</font>")
        ButterKnife.bind(this)

        setDialogsToInputFields()
        getBuilding()//setting spinner

    }

    @OnClick(R.id.block_conference)
    fun blockButton() {
        if (validateInput()) {
            validationOnDataEnteredByUser()
        }
    }

    private fun addDataToobject() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        room.email = acct!!.email
        room.purpose = purposeEditText.text.toString()
        room.fromTime = dateEditText.text.toString() + " " + fromTimeEditText.text.toString()
        room.toTime = dateEditText.text.toString() + " " + toTimeEditText.text.toString()
        room.status = "abc"
    }

    private fun setDialogsToInputFields() {

        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }

        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }

        dateEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateEditText)
        }


    }

    private fun getBuilding() {
        val mBuildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mBuildingViewModel.getBuildingList(this).observe(this, androidx.lifecycle.Observer {
            buildingListFromBackend(it)
        })
    }

    private fun blocking(room: BlockRoom) {
        val mBlockConfirmationViewModel = ViewModelProviders.of(this).get(BlockConfirmationViewModel::class.java)
        mBlockConfirmationViewModel.blockingStatus(this, room)!!.observe(this, androidx.lifecycle.Observer {
            Log.i("@@@", it.toString())
            if (it.mStatus == 0) {
                blockConfirmed(room)
            } else {
                val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity)
                builder.setTitle(getString(R.string.blockingStatus))
                val name = it.name
                val purpose = it.purpose
                builder.setMessage(
                    "Room is already Booked by Employee $name for $purpose.\nAre you sure the 'BLOCKING' is Necessary?"
                )
                builder.setPositiveButton(getString(R.string.ok_label)) { _,_ ->
                    blockConfirmed(room)
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        })
    }

    private fun blockConfirmed(room: BlockRoom) {
        val mBlockRoomViewModel = ViewModelProviders.of(this).get(BlockRoomViewModel::class.java)
        mBlockRoomViewModel.blockRoom(this, room).observe(this, androidx.lifecycle.Observer {
            val builder =
                AlertDialog.Builder(this@BlockConferenceRoomActivity)
            builder.setTitle(getString(R.string.blockingStatus))
            builder.setMessage(getString(R.string.room_is_blocked))
            builder.setPositiveButton(getString(R.string.ok)) { _,_ ->
                finish()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        })
    }

    private fun buildingListFromBackend(it: List<Building>) {
        sendDataForSpinner(it)
    }

    private fun sendDataForSpinner(it: List<Building>) {
        val items = mutableListOf<String>()
        val itemsid = mutableListOf<Int>()
        for (item in it) {
            items.add(item.buildingName!!)
            itemsid.add(item.buildingId!!.toInt())
        }
        buiding_Spinner.adapter =
            ArrayAdapter<String>(this@BlockConferenceRoomActivity, android.R.layout.simple_list_item_1, items)
        buiding_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first building
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.bId = itemsid[position]
                conferenceRoomListFromBackend(itemsid[position])
            }
        }
    }

    fun conferenceRoomListFromBackend(buildingId: Int) {
        val mBlockConferenceRoomListViewModel =
            ViewModelProviders.of(this).get(BlockConferenceRoomListViewModel::class.java)
        mBlockConferenceRoomListViewModel.getRoomList(this, buildingId)!!.observe(this, androidx.lifecycle.Observer {
            setSpinnerToConferenceList(it)
        })
    }

    private fun setSpinnerToConferenceList(it: List<BuildingConference>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()

        for (item in it) {
            conferencename.add(item.roomName!!)
            conferenceid.add(item.roomId)
        }
        conference_Spinner.adapter =
            ArrayAdapter<String>(this@BlockConferenceRoomActivity, android.R.layout.simple_list_item_1, conferencename)
        conference_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first conference room
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.cId = conferenceid[position]
            }
        }

    }

   private fun validateInput(): Boolean {
       when {
           isEmpty(fromTime_b.text.trim()) -> {
               Toast.makeText(
                   applicationContext,
                   getString(R.string.please_enter_fromtime),
                   Toast.LENGTH_SHORT
               ).show()
               return false

           }
           isEmpty(toTime_b.text.trim()) -> {
               Toast.makeText(
                   applicationContext,
                   getString(R.string.please_enter_totime),
                   Toast.LENGTH_SHORT
               ).show()
               return false
           }
           isEmpty(date_block.text.trim()) -> {
               Toast.makeText(
                   applicationContext,
                   getString(R.string.please_enter_date),
                   Toast.LENGTH_SHORT
               ).show()
               return false
           }
           Purpose.text.toString().trim().isEmpty() -> {
               Toast.makeText(
                   applicationContext,
                   getString(R.string.enter_purpose),
                   Toast.LENGTH_SHORT
               ).show()
               return false
           }
           else -> return true
       }
    }

    private fun validationOnDataEnteredByUser() {
        val minmilliseconds: Long = 900000
        val maxmilliseconds: Long = 14400000

        val startTime = fromTime_b.text.toString()
        val endTime = toTime_b.text.toString()

        val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity)
        builder.setTitle(getString(R.string.check))
        try {
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                date_block.text.toString()
            )
            if (elapsed2 < 0) {
                builder.setMessage(getString(R.string.invalid_from_time))
                builder.setPositiveButton(getString(R.string.ok_label)) {_,_ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
            } else {
                if ((minmilliseconds <= elapsed) && (maxmilliseconds >= elapsed)) {
                    blockRoom()
                } else {
                    val builder = AlertDialog.Builder(this@BlockConferenceRoomActivity).also {
                        it.setTitle(getString(R.string.check))
                        it.setMessage(getString(R.string.time_validation_message))
                    }
                    builder.setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this@BlockConferenceRoomActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun blockRoom() {
        addDataToobject()
        blocking(room)
    }
}
