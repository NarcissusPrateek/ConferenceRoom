package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
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
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.BlockRoom
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BlockRoomViewModel
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_registration.*
import kotlinx.android.synthetic.main.activity_spinner.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Suppress("NAME_SHADOWING", "DEPRECATION")
class BlockConferenceRoomActivity : AppCompatActivity() {

    /**
     * Declaring Global variables and butterknife
     */
    @BindView(R.id.fromTime_b)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime_b)
    lateinit var toTimeEditText: EditText
    @BindView(R.id.date_block)
    lateinit var dateEditText: EditText
    @BindView(R.id.Purpose)
    lateinit var purposeEditText: EditText
    private lateinit var mBlockRoomViewModel: BlockRoomViewModel
    var room = BlockRoom()
    private lateinit var mBuildingViewModel: BuildingViewModel
    private lateinit var progressDialog: ProgressDialog
    private var mBuildingName = "Select Building"
    private var mRoomName = "Select Room"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spinner)
        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Block) + "</font>")
        ButterKnife.bind(this)
        init()
        observeData()
        setDialogsToInputFields()
        getBuilding()//setting spinner

    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mBuildingViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mBlockRoomViewModel = ViewModelProviders.of(this).get(BlockRoomViewModel::class.java)
        textChangeListenerOnDateEditText()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        textChangeListenerOnPurposeEditText()
    }
    /**
     * observing data for building list
     */
    private fun observeData() {
        mBuildingViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            if (it.isEmpty()) {
                Toast.makeText(this, getString(R.string.empty_building_list), Toast.LENGTH_SHORT).show()
            } else {
                buildingListFromBackend(it)
            }
        })

        mBuildingViewModel.returnMBuildingFailure().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
                finish()
            }
        })

        // observer for Block room

        mBlockRoomViewModel.returnSuccessForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_is_blocked), Toast.LENGTH_SHORT, true).show()
            finish()
        })

        mBlockRoomViewModel.returnResponseErrorForBlockRoom().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
            }
        })

        // observer for block confirmation
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
                    "Room is Booked by Employee $name for $purpose.\nAre you sure the 'BLOCKING' is Necessary?"
                )
                builder.setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                    blockConfirmed(room)
                }
                builder.setNegativeButton(getString(R.string.no)) { _, _ ->
                    /**
                     * do nothing
                     */
                }
                builder.setCancelable(false)
                val dialog: AlertDialog = builder.create()
                dialog.show()
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        })

        mBlockRoomViewModel.returnResponseErrorForConfirmation().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
            }
        })

        // observer for conference room list

        mBlockRoomViewModel.returnConferenceRoomList().observe(this, Observer {
            progressDialog.dismiss()
            setSpinnerToConferenceList(it)
        })

        mBlockRoomViewModel.returnResponseErrorForConferenceRoom().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
            }
        })

    }

    /**
     * function will invoke whenever the block button is pressed
     */

    @OnClick(R.id.block_conference)
    fun blockButton() {
        if (validateInput()) {
            validationOnDataEnteredByUser()
        }
    }
    /**
     *  set values to the different properties of object which is required for api call
     */
    private fun addDataToObject() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        room.email = acct!!.email
        room.purpose = purposeEditText.text.toString()
        room.fromTime = dateEditText.text.toString() + " " + fromTimeEditText.text.toString()
        room.toTime = dateEditText.text.toString() + " " + toTimeEditText.text.toString()
        room.status = "abc"

    }

    /**
     * set the date and time picker
     */
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
    /**
     * function calls the ViewModel of getbuilding
     */
    private fun getBuilding() {
        progressDialog.show()

        // make api call
        mBuildingViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * function calls the ViewModel of blocking
     */
    private fun blocking(room: BlockRoom) {
        progressDialog.show()
        mBlockRoomViewModel.blockingStatus(room, getUserIdFromPreference(), getTokenFromPreference())
    }
    /**
     * function calls the ViewModel of blockingConfirmed
     */
    private fun blockConfirmed(mRoom: BlockRoom) {
        progressDialog.show()
        mBlockRoomViewModel.blockRoom(mRoom, getUserIdFromPreference(), getTokenFromPreference())
    }
    /**
     * function calls the ViewModel of getConference for the Selected Building
     */
    fun conferenceRoomListFromBackend(buildingId: Int) {
        progressDialog.show()
        mBlockRoomViewModel.getRoomList(buildingId, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     *Setting the empty text to the Spinner if the data is empty
     */
    private fun buildingListFromBackend(it: List<Building>) {
        if (it.isEmpty()) {
            Toasty.info(this, R.string.empty_building_list, Toast.LENGTH_SHORT, true).show()
        } else {
            sendDataForSpinner(it)
        }
    }

    /**
     * Setting the Building Data to the Spinner if the data is not empty
     */
    private fun sendDataForSpinner(it: List<Building>) {
        val items = mutableListOf<String>()
        val itemsId = mutableListOf<Int>()
        items.add("Select Building")
        itemsId.add(-1)
        for (item in it) {
            items.add(item.buildingName!!)
            itemsId.add(item.buildingId!!.toInt())
        }
        buiding_Spinner.adapter =
            ArrayAdapter<String>(this@BlockConferenceRoomActivity, R.layout.spinner_icon, R.id.spinner_text, items)
        buiding_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first building
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.bId = itemsId[position]
                mBuildingName = items[position]
                error_spinner_building_text_view.visibility = View.INVISIBLE
                conferenceRoomListFromBackend(itemsId[position])
            }
        }
    }

    /**
     * Setting the Conference Data to the Spinner
     */
    private fun setSpinnerToConferenceList(it: List<BuildingConference>) {
        val conferencename = mutableListOf<String>()
        val conferenceid = mutableListOf<Int>()
        if (it.isEmpty()) {
            conferencename.add("No Room in the Buildings")
            conferenceid.add(-1)
        } else {
            conferencename.add("Select Room")
        }
        conferenceid.add(-1)
        for (item in it) {
            conferencename.add(item.roomName!!)
            conferenceid.add(item.roomId)
        }
        conference_Spinner.adapter =
            ArrayAdapter<String>(
                this@BlockConferenceRoomActivity,
                R.layout.spinner_icon,
                R.id.spinner_text,
                conferencename
            )
        conference_Spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                /**
                 * It selects the first conference room
                 */
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                room.cId = conferenceid[position]
                mRoomName = conferencename[position]
                error_spinner_room_text_view.visibility = View.INVISIBLE
            }
        }

    }


    /**
     * validate from time field
     */
    private fun validateFromTime(): Boolean {
        val input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_from_time_layout.error = null
            true
        }
    }

    /**
     * validate to time field
     */
    private fun validateToTime(): Boolean {
        val input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_to_time_layout.error = null
            true
        }
    }

    /**
     * validate date field
     */
    private fun validateDate(): Boolean {
        val input = dateEditText.text.toString().trim()
        return if (input.isEmpty()) {
            block_date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            block_date_layout.error = null
            true
        }
    }

    /**
     * validate purpose field
     */
    private fun validatePurpose(): Boolean {
        val input = purposeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            purpose_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            purpose_layout.error = null
            true
        }
    }

    /**
     * validate building spinner
     */
    private fun validateBuildingSpinner(): Boolean {
        return if (mBuildingName == "Select Building") {
            error_spinner_building_text_view.visibility = View.VISIBLE
            false
        } else {
            error_spinner_building_text_view.visibility = View.INVISIBLE
            true
        }
    }

    /**
     * validate conference room spinner
     */
    private fun validateRoomSpinner(): Boolean {
        return if (mRoomName == "Select Room") {
            error_spinner_room_text_view.visibility = View.VISIBLE
            false
        } else {
            error_spinner_room_text_view.visibility = View.INVISIBLE
            true
        }
    }

    /**
     * validate the purpose using regrex
     */

    private fun validatePurposeRegrex():Boolean{

        val purposePattern: String = "^[A-Za-z]+"
        val pattern: Pattern = Pattern.compile(purposePattern)
        val matcher: Matcher = pattern.matcher(purposeEditText.text)
        //        return matcher.matches()
        return if(!matcher.matches()){
            purpose_layout.error = getString(R.string.invalid_purpose_name)
            false
        }
        else{
            true
        }
    }

    private fun validateInput(): Boolean {
        if (!validateFromTime() or !validateToTime() or !validateDate() or !validatePurpose() or !validateBuildingSpinner() or !validateRoomSpinner() or !validatePurposeRegrex()) {
            return false
        }
        return true
    }

    /**
     * Validating the from-time and to-time having the conditions
     */
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
                builder.setPositiveButton(getString(R.string.ok_label)) { _, _ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()

            }
            if (room.cId!!.compareTo(-1) == 0) {
                Toast.makeText(this, R.string.invalid_conference_room, Toast.LENGTH_SHORT).show()
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

    /**
     * Adding the data to the objects
     */
    private fun blockRoom() {
        addDataToObject()
        blocking(room)
    }

    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        val dialog = GetAleretDialog.getDialog(
            this, getString(R.string.session_expired), "Your session is expired!\n" +
                    getString(R.string.session_expired_messgae)
        )
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        val mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }
    /**
     * get token and userId from local storage
     */
    private fun getTokenFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("Token", "Not Set")!!
    }

    private fun getUserIdFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("UserId", "Not Set")!!
    }

    /**
     * add text change listener for the start time edit text
     */
    private fun textChangeListenerOnFromTimeEditText() {
        fromTimeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateFromTime()
            }
        })
    }
    /**
     * add text change listener for the end time edit text
     */
    private fun textChangeListenerOnToTimeEditText() {
        toTimeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateToTime()
            }
        })
    }
    /**
     * add text change listener for the start edit text
     */
    private fun textChangeListenerOnDateEditText() {
        dateEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateDate()

            }
        })
    }

    /**
     * add text change listener for the purpose edit text
     */
    private fun textChangeListenerOnPurposeEditText() {
        purposeEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePurpose()
            }
        })
    }

}