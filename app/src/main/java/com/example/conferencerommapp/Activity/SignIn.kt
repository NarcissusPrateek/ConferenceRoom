package com.example.conferencerommapp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife

import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Helper.Constants
import com.example.conferencerommapp.ViewModel.CheckRegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.SignInButton

class SignIn : AppCompatActivity() {

    var RC_SIGN_IN = 0
    @BindView(R.id.sign_in_button)
    lateinit var signInButton: SignInButton

    @BindView(R.id.l1) lateinit var linearLayoutUp: LinearLayout

    @BindView(R.id.l2) lateinit var linearLayoutDown: LinearLayout
    var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        ButterKnife.bind(this)
        initialize()
        signInButton!!.setOnClickListener(View.OnClickListener {
            startintentToGoogle()
        })
    }

    /**
     * function intialize all items of UI, sharedPreference and calls the setAnimationToLayout function to set the animation to the layouts
     */
    fun initialize() {
        linearLayoutUp = findViewById(R.id.l1)
        linearLayoutDown = findViewById(R.id.l2)
        signInButton = findViewById(R.id.sign_in_button)
        prefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        initializeGoogleSignIn()
        setAnimationToLayout()
    }

    /**
     * function will starts a explict intent for the google sign in
     */
    fun startintentToGoogle() {
        val signInIntent = mGoogleSignInClient!!.getSignInIntent()
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * function will initialize the GoogleSignInClient
     */
    fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * set aniimation for the login activity
     */
    fun setAnimationToLayout() {

        linearLayoutUp.setAnimation(AnimationUtils.loadAnimation(this, R.anim.uotodown));
        linearLayoutDown.setAnimation(AnimationUtils.loadAnimation(this, R.anim.downtoup));
    }


    /**
     * fuction will automatically invoked once the control will return from the explict intent and than call another
     * method to do further task
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /**
     * function will call a another function which connects to the backend.
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            checkRegistration(account!!.email.toString())
        } catch (e: ApiException) {
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.statusCode)
        }

    }

    /**
     * on back pressed the function will clear the activity stack and will close the application
     */
    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }

    fun checkStatus(account: GoogleSignInAccount) {
        var status = prefs!!.getInt(Constants.EXTRA_REGISTERED, 0)
        if(status == 1) {
            startActivity(Intent(this@SignIn, UserBookingsDashboardActivity::class.java))
            finish()
        } else {
            checkRegistration(account.email.toString())
        }
    }

    /**
     * this function will check whether the user is registered or not
     * if not registered than make an intent to registration activity
     */
    fun checkRegistration(email: String) {
        var mCheckRegistrationViewModel = ViewModelProviders.of(this).get(CheckRegistrationViewModel::class.java)
        mCheckRegistrationViewModel.checkRegistration(this, email).observe(this, Observer {
            setValueForSharedPreference(it)
        })
    }

    /**
     * this function will intent to some activity according to the received data from backend
     */
    fun intentToNextActivity(code: Int?) {
        when (code) {
            11, 10, 2, 12 -> {
                startActivity(Intent(this@SignIn, UserBookingsDashboardActivity::class.java))
            }
            0 -> {
                startActivity(Intent(this@SignIn, RegistrationActivity::class.java))
            }
            else -> {
                val builder = AlertDialog.Builder(this@SignIn)
                builder.setTitle(getString(R.string.error))
                builder.setMessage(getString(R.string.restart_app))
                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    finish()
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
    }

    /**
     * a function which will set the value in shared preference
     */
    fun setValueForSharedPreference(status: Int) {
        var code = status
//        if(code != 0) {
//            val editRegistrationStatus = prefs!!.edit()
//            editRegistrationStatus.putInt(Constants.EXTRA_REGISTERED, 1)
//            editRegistrationStatus.apply()
//        }
        val editor = prefs!!.edit()
        editor.putInt("Code", code!!)
        editor.apply()
        intentToNextActivity(code)
    }
}
