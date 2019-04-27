package com.example.conferencerommapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.ViewModel.RegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.regex.Matcher
import java.util.regex.Pattern


@Suppress("DEPRECATION")
class RegistrationActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var mRegistrationViewModel: RegistrationViewModel
    @BindView(R.id.edittext_id)
    lateinit var employeeIdEditText: EditText
    @BindView(R.id.textView_name)
    lateinit var employeeNameEditText: EditText
    @BindView(R.id.spinner)
    lateinit var employeeRoleSpinner: Spinner
    private lateinit var progressDialog: ProgressDialog
    val mEmployee = Employee()
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Registration) + "</font>")
        init()
        observeData()
        initializeFields()
    }

    /**
     * function will invoked when user hit the reigister button
     */
    @OnClick(R.id.button_add)
    fun register() {
        if (validateInput()) {
            setDataToEmployeeObjct()
            addEmployee()
        }
    }

    /**
     * validate employee id
     */
    private fun validateId(): Boolean {
        val input = employeeIdEditText.text.toString().trim()
        return if(input.isEmpty()) {
            employee_id_layout.error = getString(R.string.field_cant_be_empty)
            false
        } else {
            employee_id_layout.error = null
            true
        }
    }

    /**
     * validate spinner
     */
    private fun validateRoleSpinner(): Boolean {
        return if(mEmployee.role.toString() == "Select Role") {
            error_role_spinner_text_view.visibility = View.VISIBLE
            false
        }else {
            error_role_spinner_text_view.visibility = View.GONE
            true
        }
    }
    /**
     * validate the empId using regrex
     */

    private fun validateEmpIdRegrex():Boolean{

            val empIdPattern: String = "^[Nn][Ll][Ii][-][0-9][0-9][0-9]"
            val pattern: Pattern = Pattern.compile(empIdPattern)
            val matcher: Matcher = pattern.matcher(employeeIdEditText.text)
    //        return matcher.matches()
        return if(!matcher.matches()){
            employee_id_layout.error = "Invalid Employee Id"
            false
        }
        else{
            true
            }
        }
    /**
     * validate all data of input fields entered by user
     */
    private fun validateInput(): Boolean {
        if(!validateId() or !validateRoleSpinner() or !validateEmpIdRegrex()) {
            return false
        }
        return true
    }

    /**
     * get the view according to the id from layout file
     */
    private fun initializeFields() {
        pref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        setValueToUserNameField()
        getValueFromRoleSpinner()
    }

    private fun setValueToUserNameField() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            employeeNameEditText.setText(account.displayName.toString())
        }
    }

    /**
     * function will set the spinner for selecting role in the company
     */
    private fun getValueFromRoleSpinner() {

        val employeeRole = resources.getStringArray(R.array.role)
        var adapter = ArrayAdapter<String>(this, R.layout.role_spinner_icon, R.id.spinner_text, employeeRole)
        employeeRoleSpinner.adapter = adapter
        employeeRoleSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                mEmployee.role = employeeRole[position]
            }
            override fun onNothingSelected(adapterView: AdapterView<*>) {
                //capacity = getString(R.string.select_room_capacity)
            }
        }
    }


    /**
     * function will set the information entered by user to gloabal object employee
     */
    private fun setDataToEmployeeObjct() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if (acct != null) {
            mEmployee.employeeId = edittext_id.text.toString().trim()
            mEmployee.name = textView_name.text.toString().trim()
            mEmployee.activationCode = "abc"
            mEmployee.email = acct.email.toString().trim()
            mEmployee.verified = false
        } else {
            Toast.makeText(this, "unable to get the data fro server", Toast.LENGTH_SHORT).show()
        }
    }


    /**
     * if user hit the backbutton without registering himself than the user will logout and redirect to the sign in screen
     */
    override fun onBackPressed() {
        super.onBackPressed()
        signOut()
    }

    /**
     * this function will call some other viewmodel function which will make a request to backend for adding the employee
     */
    private fun addEmployee() {
        progressDialog.show()
        mRegistrationViewModel.addEmployee(mEmployee, getUserIdFromPreference(), getTokenFromPreference())
    }

    /**
     * observe data from server
     */
    private fun observeData() {
        mRegistrationViewModel.returnSuccessForRegistration().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.registered_successfully), Toast.LENGTH_SHORT, true).show()
            setValueInSharedPreference()
            startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
        })
        mRegistrationViewModel.returnFailureForRegistration().observe(this, Observer {
            progressDialog.dismiss()
            if(it == getString(R.string.invalid_token)) {
                showAlert()
            }else {
                ShowToast.show(this, it)
            }
        })
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message_processing), this)
        mRegistrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
    }

    /**
     * this function sets the values in sharedPreference
     */
    private fun setValueInSharedPreference() {
        val editor = pref.edit()
        editor.putInt(Constants.EXTRA_REGISTERED, 1)
        editor.apply()
    }


    /**
     * show dialog for session expired
     */
    private fun showAlert() {
        var dialog = GetAleretDialog.getDialog(this, getString(R.string.session_expired), "Your session is expired!\n" +
                getString(R.string.session_expired_messgae))
        dialog.setPositiveButton(R.string.ok) { _, _ ->
            signOut()
        }
        var builder = GetAleretDialog.showDialog(dialog)
        ColorOfDialogButton.setColorOfDialogButton(builder)
    }

    /**
     * sign out from application
     */
    private fun signOut() {
        var mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient!!.signOut()
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