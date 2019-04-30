package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.AddConferenceRoom
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.AddConferenceRoomViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_adding_conference.*

@Suppress("DEPRECATION")
class AddingConference : AppCompatActivity() {
    /**
     * Declaring Global variables and binned butter knife
     */
    var capacity = "Select Room Capacity"
    @BindView(R.id.conference_Name)
    lateinit var conferenceRoomEditText: EditText

    @BindView(R.id.conference_Capacity)
    lateinit var capacitySpinner: Spinner
    private lateinit var mAddConferenceRoomViewModel: AddConferenceRoomViewModel
    private var mConferenceRoom = AddConferenceRoom()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adding_conference)
        ButterKnife.bind(this)
        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Add_Room) + "</font>")
        textChangeListenerOnRoomName()
        setSpinnerForCapacity()
        init()
        observeData()
    }
    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mAddConferenceRoomViewModel = ViewModelProviders.of(this).get(AddConferenceRoomViewModel::class.java)
    }
    /**
     * observing data for adding conference
     */
    fun observeData() {
        mAddConferenceRoomViewModel.returnSuccessForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.room_add_success), Toast.LENGTH_SHORT, true).show()
            finish()
        })
        mAddConferenceRoomViewModel.returnFailureForAddingRoom().observe(this, Observer {
            progressDialog.dismiss()
            if (it == getString(R.string.invalid_token)) {
                showAlert()
            } else {
                ShowToast.show(this, it)
            }
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
     * function will set the BlockConferenceRoomActivity Value for the capacity
     */
    private fun setSpinnerForCapacity() {
        val options = mutableListOf("Select Room Capacity", "2", "4", "6", "8", "10", "12", "14", "16")
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_icon, R.id.spinner_text, options)
        capacitySpinner.adapter = adapter
        capacitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                capacity = options[position]
                error_capacity_text_view.visibility = View.INVISIBLE
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {
                capacity = getString(R.string.select_room_capacity)
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
     * validation for room employeeList
     */
    private fun validateRoomName(): Boolean {
        val input = conferenceRoomEditText.text.toString().trim()
        return if (input.isEmpty()) {
            room_name_layout_name.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            room_name_layout_name.error = null
            true
        }
    }
    /**
     * validation for spinner
     */
    private fun validateSpinner(): Boolean {
        return if (capacity == getString(R.string.select_room_capacity)) {
            error_capacity_text_view.visibility = View.VISIBLE
            false
        } else {
            error_capacity_text_view.visibility = View.INVISIBLE
            true
        }
    }
    /**
     * validate all input fields
     */
    private fun validateInputs(): Boolean {
        if (!validateRoomName() or !validateSpinner()) {
            return false
        }
        return true
    }
    /**
     * function calls the ViewModel of addingConference and data into the database
     */
    private fun addRoom(mConferenceRoom: AddConferenceRoom) {
        progressDialog.show()
        mAddConferenceRoomViewModel.addConferenceDetails(mConferenceRoom, getUserIdFromPreference(), getTokenFromPreference())
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
    fun getTokenFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("Token", "Not Set")!!
    }

    fun getUserIdFromPreference(): String {
        return getSharedPreferences("myPref", Context.MODE_PRIVATE).getString("UserId", "Not Set")!!
    }


    /**
     * add text change listener for the room name
     */
    private fun textChangeListenerOnRoomName() {
        conferenceRoomEditText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // nothing here
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nothing here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateRoomName()
            }
        })
    }
}
