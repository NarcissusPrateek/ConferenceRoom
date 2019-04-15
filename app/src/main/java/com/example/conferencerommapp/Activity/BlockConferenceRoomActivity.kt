
package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.os.Bundle
import android.text.Html.fromHtml
import android.text.TextUtils.isEmpty
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
import com.example.conferencerommapp.BuildingConference
import com.example.conferencerommapp.Helper.ConvertTimeInMillis
import com.example.conferencerommapp.Helper.DateAndTimePicker
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
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
    lateinit var mBlockRoomViewModel: BlockRoomViewModel
    var room = BlockRoom()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Block) + "</font>")
        ButterKnife.bind(this)

        mBlockRoomViewModel = ViewModelProviders.of(this).get(BlockRoomViewModel::class.java)
        setDialogsToInputFields()
        getBuilding()//setting spinner

    }

    @OnClick(R.id.block_conference)
    fun blockButton() {
        if (validateInput()) {
            validationOnDataEnteredByUser()
        }
    }

    private fun addDataToObject() {
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

        /**
         * get progress dialog
         */
        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        val mBuildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        progressDialog.show()
        mBuildingViewModel.getBuildingList()

        mBuildingViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if(it.isEmpty()) {

            }else {
                buildingListFromBackend(it)
            }
        })

        mBuildingViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()
            // some message according to the error code from backend
        })
    }

    private fun blocking(room: BlockRoom) {

        /**
         * get progress Dialog
         */
        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        progressDialog.show()
        mBlockRoomViewModel.blockingStatus(room)

        mBlockRoomViewModel.returnSuccessForConfirmation().observe(this, Observer {
            progressDialog.dismiss()
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

        mBlockRoomViewModel.returnResponseErrorForConfirmation().observe(this, Observer {
            progressDialog.dismiss()
            // some message according to the error code from server
        })
    }

    private fun blockConfirmed(mRoom: BlockRoom) {

        /**
         * get progress dialog
         */
        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        progressDialog.show()
        mBlockRoomViewModel.blockRoom(mRoom)

        mBlockRoomViewModel.returnSuccessForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
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

        mBlockRoomViewModel.returnResponseErrorForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            // some message according to the error code
        })
    }

    private fun buildingListFromBackend(it: List<Building>) {
        sendDataForSpinner(it)
    }

    private fun sendDataForSpinner(it: List<Building>) {
        val items = mutableListOf<String>()
        val itemsId = mutableListOf<Int>()
        for (item in it) {
            items.add(item.buildingName!!)
            itemsId.add(item.buildingId!!.toInt())
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
                room.bId = itemsId[position]
                conferenceRoomListFromBackend(itemsId[position])
            }
        }
    }

    fun conferenceRoomListFromBackend(buildingId: Int) {
        val progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBlockRoomViewModel.getRoomList(buildingId)
        progressDialog.dismiss()
        mBlockRoomViewModel.returnConferenceRoomList().observe(this, Observer {
            progressDialog.dismiss()
            if (it.isEmpty()) {
                // do something
            }else {
                setSpinnerToConferenceList(it)
            }
        })
        mBlockRoomViewModel.returnResponseErrorForConferenceRoom().observe(this, Observer {
            progressDialog.dismiss()
            // some message according to the error code from backend
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
        addDataToObject()
        blocking(room)
    }
}
