package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.R
import com.example.conferencerommapp.RegistrationActivity
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.CheckRegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount


class SplashScreen : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog
    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        /**
         * for splash screen
         */
        val logoHandler: Handler = Handler()
        val logoRunnable: Runnable = Runnable {
            val account = GoogleSignIn.getLastSignedInAccount(this)
            prefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
            if (account != null) {
                checkRegistration(account.email.toString())
            } else {
                signIn()
            }
        }
        logoHandler.postDelayed(logoRunnable, 3000)
    }

    /**
     * function make a request to backend for checking whether the user is registered or not
     */
    fun checkRegistration(email: String) {
        var mCheckRegistrationViewModel = ViewModelProviders.of(this).get(CheckRegistrationViewModel::class.java)
        mCheckRegistrationViewModel.checkRegistration(this, email).observe(this, Observer {
            setValueForSharedPreference(it)
        })
    }

    /**
     * pass the intent for the SignIn Activity
     */
    fun signIn() {
        startActivity(Intent(applicationContext, SignIn::class.java))
        finish()
    }

    /**
     * get data from shared preference and if the user is already registered than redirect the control to UserBookingsDashboardActivity
     */
    fun checkStatus(account: GoogleSignInAccount) {

        var status = prefs!!.getInt(Constants.EXTRA_REGISTERED, 0)
        if (status == 1) {
            startActivity(Intent(this@SplashScreen, UserBookingsDashboardActivity::class.java))
            finish()
        } else {
            checkRegistration(account.email.toString())
        }
    }
    /**
     * according to the backend status function will redirect control to some other activity
     */
    fun goToNextActivity(code: Int?) {
        when (code) {
            11, 10, 2, 12 -> {
                startActivity(Intent(this@SplashScreen, UserBookingsDashboardActivity::class.java))
                finish()
            }
            0 -> {
                startActivity(Intent(this@SplashScreen, RegistrationActivity::class.java))
                finish()
            }
            else -> {
                val builder = AlertDialog.Builder(this@SplashScreen)
                builder.setTitle("Error!")
                builder.setMessage("Plese Restart the application..")
                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    finish()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }

        }
    }

    /**
     * set value in shared preference
     */
    fun setValueForSharedPreference(status: Int) {
        var code = status
        val editor = prefs!!.edit()
        editor.putInt("Code", code!!)
        editor.apply()
        goToNextActivity(code)
    }
}


