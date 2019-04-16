package com.example.conferencerommapp.Activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ShowToast
import com.example.conferencerommapp.R
import com.example.conferencerommapp.RegistrationActivity
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.CheckRegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import kotlinx.android.synthetic.main.activity_splash_screen.*


class SplashScreen : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mCheckRegistrationViewModel: CheckRegistrationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        init()
        observeData()
        val logoHandler = Handler()
        val logoRunnable = Runnable {
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
    private fun checkRegistration(email: String) {
        progressDialog.show()
        mCheckRegistrationViewModel.checkRegistration(email)
    }

    /**
     * initialize all lateinit variables
     */
    fun init() {
        progressDialog =  GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        mCheckRegistrationViewModel = ViewModelProviders.of(this).get(CheckRegistrationViewModel::class.java)
    }

    private fun observeData() {
        mCheckRegistrationViewModel.returnSuccessCode().observe(this, Observer {
            progressDialog.dismiss()
            setValueForSharedPreference(it)
        })
        mCheckRegistrationViewModel.returnFailureCode().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
            finish()
        })
    }
    /**
     * pass the intent for the SignIn Activity
     */
    private fun signIn() {
        startActivity(Intent(applicationContext, SignIn::class.java))
        finish()
    }

    /**
     * according to the backend status function will redirect control to some other activity
     */
    private fun goToNextActivity(code: Int?) {
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
                builder.setMessage(getString(R.string.restart_app))
                builder.setPositiveButton(getString(R.string.ok)) { _,_ ->
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
    private fun setValueForSharedPreference(status: Int) {
        val editor = prefs.edit()
        editor.putInt("Code", status)
        editor.apply()
        goToNextActivity(status)
    }
}


