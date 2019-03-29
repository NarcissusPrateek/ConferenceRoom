package com.example.conferencerommapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.ViewModel.RegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import fr.ganfra.materialspinner.MaterialSpinner
import kotlinx.android.synthetic.main.activity_registration.*


@Suppress("DEPRECATION")
class RegistrationActivity : AppCompatActivity() {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var mRegistrationViewModel: RegistrationViewModel
    @BindView(R.id.edittext_id)
    lateinit var employeeIdEditText: EditText
    @BindView(R.id.textView_name)
    lateinit var employeeNameEditText: EditText
    @BindView(R.id.spinner)
    lateinit var employeeRoleSpinner: MaterialSpinner
    val mEmployee = Employee()
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        ButterKnife.bind(this)

        val actionBar = supportActionBar
        actionBar!!.title = Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Registration) + "</font>")

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
     * validate all data of input fields entered by user
     */
    private fun validateInput(): Boolean {
        return when {
            employeeIdEditText.text.trim().isEmpty() -> {
                Toast.makeText(this@RegistrationActivity, "Invalid name", Toast.LENGTH_SHORT).show()
                false
            }
            employeeNameEditText.text.trim().isEmpty() -> {
                Toast.makeText(this@RegistrationActivity, "Invalid Id", Toast.LENGTH_SHORT).show()
                false
            }
            mEmployee.role.toString() == "Select role" -> {
                Toast.makeText(this@RegistrationActivity, "Invalid role", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
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
        employeeRoleSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, employeeRole)
        employeeRoleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mEmployee.role = "Intern"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mEmployee.role = spinner.getItemAtPosition(position).toString()
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
     * to signout from the application
     */
    private fun signOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@RegistrationActivity, gso)
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }

    /**
     * this function will call some other viewmodel function which will make a request to backend for adding the employee
     */
    private fun addEmployee() {
        mRegistrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
        mRegistrationViewModel.addEmployee(this, mEmployee)!!.observe(this, Observer {
            if (it == 200) {
                setValueInSharedPreference()
                startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
            }
        })
    }

    /**
     * this function sets the values in sharedPreference
     */
    private fun setValueInSharedPreference() {
        val editor = pref.edit()
        editor.putInt(Constants.EXTRA_REGISTERED, 1)
        editor.apply()
    }
}