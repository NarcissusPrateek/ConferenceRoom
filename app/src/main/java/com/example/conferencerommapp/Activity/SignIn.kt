package com.example.conferencerommapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Helper.GetAleretDialog
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.Helper.ShowToast
import com.example.conferencerommapp.ViewModel.CheckRegistrationViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class SignIn : AppCompatActivity() {

    private var RC_SIGN_IN = 0
    @BindView(R.id.l1)
    lateinit var linearLayoutUp: LinearLayout
    @BindView(R.id.l2)
    lateinit var linearLayoutDown: LinearLayout
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mCheckRegistrationViewModel: CheckRegistrationViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        ButterKnife.bind(this)
        init()
        initialize()
        observeData()
    }

    @OnClick(R.id.sign_in_button)
    fun signIn() {
        startintentToGoogle()
    }

    /**
     * function intialize all items of UI, sharedPreference and calls the setAnimationToLayout function to set the animation to the layouts
     */
    fun initialize() {
        prefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        initializeGoogleSignIn()
        setAnimationToLayout()
    }

    /**
     * function will starts a explict intent for the google sign in
     */
    private fun startintentToGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * function will initialize the GoogleSignInClient
     */
    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    /**
     * set aniimation for the login activity
     */
    private fun setAnimationToLayout() {

        linearLayoutUp.animation = loadAnimation(this, R.anim.uotodown)
        linearLayoutDown.animation = loadAnimation(this, R.anim.downtoup)
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

    /**
     * this function will check whether the user is registered or not
     * if not registered than make an intent to registration activity
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

    /**
     * observe data from server
     */
    private fun observeData() {
        //positive response from server
        mCheckRegistrationViewModel.returnSuccessCode().observe(this, Observer {
            setValueForSharedPreference(it)
        })
        // Negative response from server
        mCheckRegistrationViewModel.returnFailureCode().observe(this, Observer {
            ShowToast.show(this, it)
        })
    }

    /**
     * this function will intent to some activity according to the received data from backend
     */
    private fun intentToNextActivity(code: Int?) {
        when (code) {
            11, 10, 2, 12 -> {
                startActivity(Intent(this@SignIn, UserBookingsDashboardActivity::class.java))
            }
            0 -> {
                startActivity(Intent(this@SignIn, RegistrationActivity::class.java))
            }
            else -> {
                val builder =
                    GetAleretDialog.getDialog(this, getString(R.string.error), getString(R.string.restart_app))
                builder.setPositiveButton(getString(R.string.ok)) { _,_ ->
                    finish()
                }
                GetAleretDialog.showDialog(builder)
            }
        }
    }

    /**
     * a function which will set the value in shared preference
     */
    private fun setValueForSharedPreference(status: Int) {
        val code = status
        val editor = prefs.edit()
        editor.putInt("Code", code)
        editor.apply()
        intentToNextActivity(code)
    }
}
