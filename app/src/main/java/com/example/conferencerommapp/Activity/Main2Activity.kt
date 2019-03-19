package com.example.conferencerommapp.Activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.conferencerommapp.BlockedDashboard
import com.example.conferencerommapp.Helper.DashBoardAdapter
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import com.github.clans.fab.FloatingActionButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main2Activity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var progressDialog: ProgressDialog? = null
    var prefs: SharedPreferences? = null
    var mGoogleSignInClient: GoogleSignInClient?? = null
    var finalList = ArrayList<Manager>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)


        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Loading...")
        progressDialog!!.setCancelable(false)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        var viewH = nav_view.getHeaderView(0)
        val acct = GoogleSignIn.getLastSignedInAccount(this@Main2Activity)
        val name = acct!!.displayName
        viewH.nv_profile_name.setText("Hello, ${name}")
        val personPhoto = acct!!.getPhotoUrl()
        viewH.nv_profile_email.text = acct.email
        if (personPhoto == null) {
            viewH.nv_profile_image.setImageResource(R.drawable.cat)
        } else {
            Glide.with(applicationContext).load(personPhoto).thumbnail(1.0f).into(viewH.nv_profile_image)
        }

        val pref = getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var code = pref.getInt("Code", 10)
        if (code != 11) {
            val nav_Menu = nav_view.getMenu()
            nav_Menu.findItem(R.id.HR).setVisible(false)
        }
        if (code != 12) {
            val nav_Menu = nav_view.getMenu()
            nav_Menu.findItem(R.id.project_manager).setVisible(false)
        }
        val userinputs: FloatingActionButton = findViewById(R.id.user_input)
        userinputs.setOnClickListener {
            startActivity(Intent(this@Main2Activity, UserInputActivity::class.java))
            //finish()
        }
        progressDialog!!.show()
        loadDashBoard()
    }

    override fun onResume() {
        super.onResume()
        progressDialog!!.show()
        loadDashBoard()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()
                mGoogleSignInClient = getClient(this@Main2Activity, gso)
                mGoogleSignInClient!!.signOut()
                    .addOnCompleteListener(this) {
                        Toast.makeText(applicationContext, "Successfully signed out", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(applicationContext, SignIn::class.java))
                        finish()
                    }
            }
            R.id.HR -> {
                startActivity(Intent(this@Main2Activity, BlockedDashboard::class.java))
            }
            R.id.project_manager -> {
                startActivity(Intent(this@Main2Activity, ProjectManagerInputActivity::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun loadDashBoard() {

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val acct = GoogleSignIn.getLastSignedInAccount(applicationContext)
        var email = acct!!.email
        val conferenceService = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<List<Dashboard>> = conferenceService.getDashboard(email!!)
        requestCall.enqueue(object : Callback<List<Dashboard>> {
            override fun onFailure(call: Call<List<Dashboard>>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                empty_view.visibility = View.VISIBLE
                empty_view.setImageResource(R.drawable.error_shot)
                r1_dashboard.setBackgroundColor(Color.WHITE)
                //Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<List<Dashboard>>, response: Response<List<Dashboard>>) {
                Log.i("-------//------", response.body().toString())
                progressDialog!!.dismiss()
                if (response.isSuccessful) {
                    val dashboardItemList: List<Dashboard>? = response.body()
                    if (dashboardItemList!!.isEmpty()) {
                        empty_view.visibility = View.VISIBLE
                        r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
                    } else {
                        empty_view.visibility = View.GONE
                        finalList.clear()
                        //Log.i("-------List", dashboardItemList.toString())
                        for (item in dashboardItemList) {
                            var flag = 0
                            for (i in finalList) {
                                if (i.Purpose == item.Purpose) {
                                    i.fromlist.add(item.FromTime!!)
                                    i.Status.add(item.Status!!)
                                    flag = 1
                                    break
                                }
                            }
                            if (flag == 0) {
                                var final = Manager()
                                final.BName = item.BName
                                final.CId = item.CId
                                final.CName = item.CName
                                final.Purpose = item.Purpose
                                final.Name = item.Name
                                final.FromTime = item.FromTime
                                final.ToTime = item.ToTime
                                final.Email = item.Email
                                final.Status.add(item.Status!!)
                                final.fromlist.add(item.FromTime!!.split("T")[0])
                                //Log.i("------###", final.BName)
                                finalList.add(final)
                            }
                        }
                        dashbord_recyclerview1.adapter = DashBoardAdapter(finalList, this@Main2Activity)
                    }
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Unable to Load Booking Details.Please try again",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }

        })
    }
}