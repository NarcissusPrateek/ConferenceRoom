package com.example.conferencerommapp.Activity

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
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.RefreshToken
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BuildingViewModel
import com.example.conferencerommapp.ViewModel.RefreshTokenViewModel
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_user_inputs.*


@Suppress("DEPRECATION")
class UserInputActivity : AppCompatActivity() {


    /**
     * Some late initializer variables for storing the instances of different classes
     */

    @BindView(R.id.date)
    lateinit var dateEditText: EditText
    @BindView(R.id.fromTime)
    lateinit var fromTimeEditText: EditText
    @BindView(R.id.toTime)
    lateinit var toTimeEditText: EditText
    private lateinit var customAdapter: BuildingAdapter
    private lateinit var mBuildingsViewModel: BuildingViewModel
    @BindView(R.id.building_recycler_view)
    lateinit var mRecyclerView: RecyclerView
    lateinit var progressDialog: ProgressDialog
    lateinit var mRefreshTokenViewModel: RefreshTokenViewModel
    lateinit var mSetDataFromActivity: GetIntentDataFromActvity
    var capacity = "Select Room Capacity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_inputs)
        ButterKnife.bind(this)
        init()
        observerData()
        getViewModel()
    }

    private fun init() {
        val actionBar = supportActionBar
        actionBar!!.title =
            fromHtml("<font font-size = \"23px\" color=\"#FFFFFF\">" + getString(R.string.Booking_Details) + "</font>")
        setPickerToEditText()
        textChangeListenerOnDateEditText()
        textChangeListenerOnFromTimeEditText()
        textChangeListenerOnToTimeEditText()
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mRefreshTokenViewModel = ViewModelProviders.of(this).get(RefreshTokenViewModel::class.java)
        mBuildingsViewModel = ViewModelProviders.of(this).get(BuildingViewModel::class.java)
        mSetDataFromActivity = GetIntentDataFromActvity()
    }

    /**
     * function will attach date and time picker to the input fields
     */
    private fun setPickerToEditText() {

        /**
         * set Time picker for the edittext fromtime
         */
        fromTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, fromTimeEditText)
        }

        /**
         * set Time picker for the edittext toTime
         */
        toTimeEditText.setOnClickListener {
            DateAndTimePicker.getTimePickerDialog(this, toTimeEditText)
        }
        /**
         * set Date picker for the edittext dateEditText
         */
        dateEditText.setOnClickListener {
            DateAndTimePicker.getDatePickerDialog(this, dateEditText)
        }
        setCapacitySpinner()
    }

    /**
     * a spinner for selecting room capacity
     */
    private fun setCapacitySpinner() {
        val options = mutableListOf("Select Room Capacity","2", "4", "6", "8", "10", "12", "14", "16")
        val adapter = ArrayAdapter<String>(this, R.layout.spinner_icon, R.id.spinner_text, options)
        capacity_spinner.adapter = adapter

        capacity_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                capacity = options[position]
                error_spinner_text_view.visibility = View.INVISIBLE
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {
                capacity = getString(R.string.select_room_capacity)
            }
        }
    }

    /**
     * validations for all input fields for empty condition
     */
    private fun validateFromTime(): Boolean {
        val input = fromTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            from_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            from_time_layout.error = null
            true
        }
    }

    private fun validateToTime(): Boolean {
        val input = toTimeEditText.text.toString().trim()
        return if (input.isEmpty()) {
            to_time_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            to_time_layout.error = null
            true
        }
    }

    private fun validateDate(): Boolean {
        val input = dateEditText.text.toString().trim()
        return if (input.isEmpty()) {
            date_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            date_layout.error = null
            true
        }
    }

    private fun validateSpinner(): Boolean {
       if(capacity == getString(R.string.select_room_capacity)) {
           error_spinner_text_view.visibility = View.VISIBLE
           return false
       }
        error_spinner_text_view.visibility = View.INVISIBLE
        return true
    }

    /**
     * check validation for all input fields
     */
    private fun validate(): Boolean {

        if (!validateFromTime() or !validateToTime() or !validateDate() or !validateSpinner()) {
            return false
        }
        return true
    }


    /**
     * function will apply some validation on data entered by user
     */
    private fun validationOnDataEnteredByUser() {

        /**
         * Validate each input field whether they are empty or not
         * If the field contains no values we show a toast to user saying that the value is invalid for particular field
         */
        if (validate()) {
            validateTime(fromTimeEditText.text.toString(), toTimeEditText.text.toString())
        }
    }


    private fun validateTime(startTime: String, endTime: String) {
        val minMilliseconds: Long = 600000
        val maxMilliseconds: Long = 14400000

        /**
         * setting a alert dialog instance for the current context
         */
        try {

            /**
             * getting the values for time validation variables from method calculateTimeInMillis
             */
            val (elapsed, elapsed2) = ConvertTimeInMillis.calculateTimeInMilliseconds(
                startTime,
                endTime,
                date.text.toString()
            )

            /**
             * if the elapsed2 < 0 that means the from time is less than the current time. In that case
             * we restrict the user to move forword and show some message in alert that the time is not valid
             */
            if (elapsed2 < 0) {
                val builder = GetAleretDialog.getDialog(
                    this,
                    getString(R.string.invalid),
                    getString(R.string.invalid_fromtime)
                )
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                GetAleretDialog.showDialog(builder)

            }

            /**
             * if MIN_MILLISECONDS <= elapsed that means the meeting duration is more than 15 min
             * if the above condition is not true than we show a message in alert that the meeting duration must be greater than 15 min
             * if MAX_MILLISECONDS >= elapsed that means the meeting duration is less than 4hours
             * if the above condition is not true than we show show a message in alert that the meeting duration must be less than 4hours
             * if above both conditions are true than entered time is correct and user is allowed to go to the next actvity
             */
            else if (minMilliseconds <= elapsed) {
                goToConferenceRoomActivity()
            } else {
                val builder = GetAleretDialog.getDialog(
                    this,
                    getString(R.string.invalid),
                    getString(R.string.time_validation_message)
                )
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                }
                GetAleretDialog.showDialog(builder)
            }
        } catch (e: Exception) {
            Toast.makeText(this@UserInputActivity, getString(R.string.details_invalid), Toast.LENGTH_LONG).show()
        }
    }

    /**
     * add text change listener for the from time edit text
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
     * add text change listener for the to time edit text
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
     * add text change listener for the date edit text
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
     * pass the intent with data for the ConferenceRoomActivity
     */
    private fun goToConferenceRoomActivity() {
        mSetDataFromActivity.fromTime =
            (dateEditText.text.toString() + " " + fromTimeEditText.text.toString()).trim()
        mSetDataFromActivity.toTime =
            (dateEditText.text.toString() + " " + toTimeEditText.text.toString()).trim()
        mSetDataFromActivity.date = dateEditText.text.toString()
        mSetDataFromActivity.capacity = capacity
        val intent = Intent(this@UserInputActivity, ConferenceRoomActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mSetDataFromActivity)
        startActivity(intent)
    }

    /**
     * get token and userId from local storage
     */
    fun getTokenFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.token), getString(R.string.not_set))!!
    }

    fun getUserIdFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.user_id), getString(R.string.not_set))!!
    }
    /**
     * get access token from local storage
     */
    private fun getRefreshTokenFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.refresh_token), getString(R.string.not_set))!!
    }

    /**
     * get refresh token from local storage
     */
    private fun getAccessTokenFromPreference(): String {
        return getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).getString(getString(R.string.access_token), getString(R.string.not_set))!!
    }

    private fun setAccessAndRefreshToken(mRefreshToken: RefreshToken) {
        val editor = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE).edit()
        editor.putString(getString(R.string.token), mRefreshToken.accessToken)
        editor.putString(getString(R.string.refresh_token), mRefreshToken.refreshToken)
        editor.apply()
    }
    /**
     * show dialog for session expired!
     */
    private fun showAlert() {

        val dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        val builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }
    /**
     * sign out logic
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
     * observe data from server
     */
    private fun observerData() {
        mBuildingsViewModel.returnMBuildingSuccess().observe(this, Observer {
            progressDialog.dismiss()
            /**
             * different cases for different result from api call
             * if response is empty than show Toast
             * if response is ok than we can set data into the adapter
             */
            if (it.isEmpty()) {
                Toasty.info(this,  getString(R.string.empty_building_list), Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
                /**
                 * setting the adapter by passing the data into it and implementing a Interface BtnClickListner of BuildingAdapter class
                 */
                customAdapter = BuildingAdapter(this,
                    it!!,
                    object : BuildingAdapter.BtnClickListener {
                        override fun onBtnClick(buildingId: String?, buildingName: String?) {
                           mSetDataFromActivity.buildingId = buildingId
                            mSetDataFromActivity.buildingName = buildingName
                            validationOnDataEnteredByUser()
                        }
                    }
                )
                mRecyclerView.adapter = customAdapter
            }

        })
        // Negative response from server
        mBuildingsViewModel.returnMBuildingFailure().observe(this, Observer {
            if(it == getString(R.string.invalid_token)) {
                progressDialog.dismiss()
                showAlert()
                //mRefreshTokenViewModel.getAccessAndRefreshToken(RefreshToken(getAccessTokenFromPreference(), getRefreshTokenFromPreference()))
            } else {
                progressDialog.dismiss()
                ShowToast.show(this, it)
                finish()
            }
        })

        // positive response for refresh token and access token
        mRefreshTokenViewModel.returnAccessToken().observe(this, Observer {
            setAccessAndRefreshToken(it)
            mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
        })
        //negative response for access and refresh token
        mRefreshTokenViewModel.returnAccessTokenFailure().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.error(this, it, Toast.LENGTH_SHORT, true).show()
            finish()
        })
    }

    /**
     * get the object of ViewModel using ViewModelProviders and observers the data from backend
     */
    private fun getViewModel() {
        progressDialog.show()
        // make api call
        mBuildingsViewModel.getBuildingList(getUserIdFromPreference(), getTokenFromPreference())
    }
}

