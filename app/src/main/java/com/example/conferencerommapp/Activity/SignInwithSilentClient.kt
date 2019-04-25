package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.example.conferencerommapp.Helper.GetProgress
import com.example.conferencerommapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class SignInwithSilentClient : AppCompatActivity() {
    private var RC_SIGN_IN = 0
    @BindView(R.id.l1)
    lateinit var linearLayoutUp: LinearLayout
    @BindView(R.id.l2)
    lateinit var linearLayoutDown: LinearLayout
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var prefs: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sign_in_activity)
        ButterKnife.bind(this)
        initialize()
    }

    @OnClick(R.id.sign_in_button)
    fun signIn() {
        startIntentToGoogleSignIn()
    }

    /**
     * function intialize all items of UI, sharedPreference and calls the setAnimationToLayout function to set the animation to the layouts
     */
    fun initialize() {
        prefs = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        progressDialog = GetProgress.getProgressDialog(
            getString(com.example.conferencerommapp.R.string.progress_message_processing),
            this
        )
        setAnimationToLayout()
        initializeGoogleSignIn()
    }

    /**
     * function will starts a explict intent for the google sign in
     */
    private fun startIntentToGoogleSignIn() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    /**
     * function will initialize the GoogleSignInClient
     */
    private fun initializeGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(getString(R.string.server_client_id))
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
        mGoogleSignInClient!!.silentSignIn().addOnCompleteListener(
            this
        ) {
            handleSignInResult(it)
        }
    }

    /**
     * set aniimation for the login activity
     */
    private fun setAnimationToLayout() {
        linearLayoutUp.animation = AnimationUtils.loadAnimation(this, R.anim.uotodown)
        linearLayoutDown.animation = AnimationUtils.loadAnimation(this, R.anim.downtoup)
    }


    /**
     * function will automatically invoked once the control will return from the explict intent and than call another
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
            if (account == null) {
                Toast.makeText(this, "Not signed in", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.w("Google Sign In Error", "signInResult:failed code=" + e.statusCode)
        }
    }
}