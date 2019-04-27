package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.Html.fromHtml
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.SelectMemberViewModel
import com.google.android.material.chip.Chip
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_select_meeting_members.*

@Suppress("DEPRECATION")
class SelectMeetingMembersActivity : AppCompatActivity() {

    private val employeeList = ArrayList<EmployeeList>()
    private val selectedName = ArrayList<String>()
    private val selectedEmail = ArrayList<String>()
    lateinit var customAdapter: SelectMembers
    @BindView(R.id.search_edit_text)
    lateinit var searchEditText: EditText
    private lateinit var mSelectMemberViewModel: SelectMemberViewModel
    lateinit var progressDialog: ProgressDialog
    private lateinit var mGetIntentDataFromActivity: GetIntentDataFromActvity
    private var count = 0
    companion object {
        var selectedCapacity = 0
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_meeting_members)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.select_participipants) + "</font>")

        init()
        getViewModel()
        observeData()
        setClickListenerOnEditText()
        Log.i("------------", searchEditText.compoundDrawables.toString())
        //clear search edit text data
        searchEditText.onRightDrawableClicked {
            it.text.clear()
        }
    }

    /**
     * initialize all lateinit fields
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mGetIntentDataFromActivity = getIntentData()
        selectedCapacity = (mGetIntentDataFromActivity.capacity!!.toInt() + 1)
        mSelectMemberViewModel = ViewModelProviders.of(this).get(SelectMemberViewModel::class.java)
    }

    /**
     * observer data from ViewModel
     */
    private fun observeData() {
        // positive response from server
        mSelectMemberViewModel.returnSuccessForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            if(it.isEmpty()) {
                Toasty.info(this, "Empty EmployeeList", Toast.LENGTH_SHORT, true).show()
                finish()
            } else {
                employeeList.clear()
                employeeList.addAll(it)
                customAdapter = SelectMembers(it, object: SelectMembers.ItemClickListener {
                    override fun onBtnClick(name: String?, email: String?) {
                        addChip(name!!, email!!)
                    }

                })
                select_member_recycler_view.adapter = customAdapter
            }
        })
        // Negative response from server
        mSelectMemberViewModel.returnFailureForEmployeeList().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
                finish()
            }
        })
    }

    // call function of ViewModel which will make API call
    private fun getViewModel() {
        progressDialog.show()
        mSelectMemberViewModel.getEmployeeList(getUserIdFromPreference(), getTokenFromPreference())
    }

    @OnClick(R.id.next_activity)
    fun onClick() {
       if(selectedName.isEmpty()) {
           Toast.makeText(this, "Select Meeting Members", Toast.LENGTH_SHORT).show()
           return
       }
        var emailString = ""
        val size = selectedName.size
        selectedEmail.indices.forEach { index ->
            emailString += selectedEmail[index]
            if(index != (size - 1)) {
                emailString += ","
            }
        }
        mGetIntentDataFromActivity.emailOfSelectedEmployees = emailString
        val intent = Intent(this@SelectMeetingMembersActivity, BookingActivity::class.java)
        intent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActivity)
        startActivity(intent)
    }

    /**
     * add selected recycler item to chip and add this chip to chip group
     */
    fun addChip(name:String, email: String) {
        if(!selectedEmail.contains(email) && count < selectedCapacity) {
            val chip = Chip(this)
            chip.text = name
            chip.isCloseIconVisible = true
            chip_group.addView(chip)
            chip.setOnCloseIconClickListener {
                selectedName.remove(name)
                selectedEmail.remove(email)
                chip_group.removeView(chip)
                count--
            }
            selectedName.add(name)
            selectedEmail.add(email)
            count++
        } else {
            if(count >= selectedCapacity) {
                Toast.makeText(this, "select at max $selectedCapacity participants", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Already Selected!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    /**
     * get data from Intent
     */
    private fun getIntentData(): GetIntentDataFromActvity {
        return intent.extras!!.get(Constants.EXTRA_INTENT_DATA) as GetIntentDataFromActvity
    }

    /**
     * clear text in search bar whenever clear drawable clicked
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun EditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
        this.setOnTouchListener { v, event ->
            var hasConsumed = false
            if (v is EditText && event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
            hasConsumed
        }
    }

    /**
     * take input from edit text and set addTextChangedListener
     */
    private fun setClickListenerOnEditText() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing Here
                 */
            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                /**
                 * Nothing here
                 */
            }

            override fun afterTextChanged(editable: Editable) {

                filter(editable.toString())
            }
        })
    }

    /**
     * filter matched data from employee list and set updated list to adapter
     */
    fun filter(text: String) {
        val filterName = java.util.ArrayList<EmployeeList>()
        for (s in employeeList) {
            if (s.name!!.toLowerCase().contains(text.toLowerCase())) {
                filterName.add(s)
            }
        }
        customAdapter.filterList(filterName)
    }

    /**
     * show dialog for session expired
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
}
