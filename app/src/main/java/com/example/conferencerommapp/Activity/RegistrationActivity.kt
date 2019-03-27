package com.example.conferencerommapp

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
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.Model.Employee
import com.example.conferencerommapp.ViewModel.RegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : AppCompatActivity() {

    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var mRegistrationViewModel: RegistrationViewModel
    lateinit var employeeIdEdittext: EditText
    lateinit var employeeNameEditText: EditText
    lateinit var addEmployeeButton: Button
    val mEmployee = Employee()
    lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val actionBar = supportActionBar
        actionBar!!.setTitle(Html.fromHtml("<font color=\"#FFFFFF\">" + getString(R.string.Registration) + "</font>"))

        initializeFields()

        addEmployeeButton.setOnClickListener(View.OnClickListener {
                if(validateInput()) {
                        setDataToEmployeeObjct()
                        addEmployee()
                }
        })
    }

    /**
     * validate all data of input fields entered by user
     */
    fun validateInput(): Boolean {
        if (employeeIdEdittext.text.trim().isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "Invalid name", Toast.LENGTH_SHORT).show()
            return false
        } else if (employeeNameEditText.text.trim().isEmpty()) {
            Toast.makeText(this@RegistrationActivity, "Invalid Id", Toast.LENGTH_SHORT).show()
            return false
        } else if (mEmployee.Role.toString().equals("Select Role")) {
            Toast.makeText(this@RegistrationActivity, "Invalid Role", Toast.LENGTH_SHORT).show()
            return false
        }else {
            return true
        }
    }

    /**
     * get the view according to the id from layout file
     */
    fun initializeFields() {
        employeeNameEditText = findViewById(R.id.textView_name)
        employeeIdEdittext = findViewById(R.id.edittext_id)
        addEmployeeButton = findViewById(R.id.button_add)
        pref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        setValueToUserNameField()
        getValueFromRoleSpinner()
    }

    fun setValueToUserNameField() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null) {
            employeeNameEditText.setText(account.displayName.toString())
        }
    }


    /**
     * function will set the spinner for selecting role in the company
     */
    fun getValueFromRoleSpinner() {
        var options = arrayOf(
            "Intern",
            "SDE-1",
            "SDE-2",
            "SDE-3",
            "Principal Engineer",
            "Project Manager",
            "HR",
            "CEO",
            "CTO",
            "COO"
        )

        spinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                mEmployee.Role = "Intern"
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mEmployee.Role = spinner.getItemAtPosition(position).toString()
            }
        }
    }


    /**
     * function will set the information entered by user to gloabal object employee
     */
    fun setDataToEmployeeObjct() {
        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        if(acct != null) {
            mEmployee.EmpId = edittext_id.text.toString().trim()
            mEmployee.Name = textView_name.text.toString().trim()
            mEmployee.ActivationCode = "abc"
            mEmployee.Email = acct!!.email.toString().trim()
            mEmployee.Verified = false
        }else {
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
    fun signOut() {
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
    fun addEmployee() {
        mRegistrationViewModel = ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
        mRegistrationViewModel.addEmployee(this, mEmployee)!!.observe(this, Observer {
            if (it == 200) {
                setValueInSharedPreference(it)
                startActivity(Intent(this, UserBookingsDashboardActivity::class.java))
            }
        })
    }

    /**
     * this function sets the values in sharedPreference
     */
    fun setValueInSharedPreference(status: Int) {
        val editor = pref!!.edit()
        editor.putInt(Constants.EXTRA_REGISTERED, 1)
        editor.apply()
    }
}