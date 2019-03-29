package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.example.conferencerommapp.Helper.DashBoardAdapter
import com.example.conferencerommapp.Helper.GoogleGSO
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BookingDashboardViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.view.*

class UserBookingsDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var finalList = ArrayList<Manager>()
    lateinit var mBookingDashboardViewModel: BookingDashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        ButterKnife.bind(this)
        setNavigationViewItem()
        mBookingDashboardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        loadDashboard()
    }

    @OnClick(R.id.user_input)
    fun userInputActivityFloatingActionButton(){
        startActivity(Intent(this@UserBookingsDashboardActivity, UserInputActivity::class.java))
    }
    /**
     * on pressing of back button this function will clear the activity stack and close the application
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    /**
     * this function will set action to the item in navigation drawer like HR or Logout or Project Manager
     */
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                signOut()
            }
            R.id.HR -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, BlockedDashboard::class.java))
            }
            R.id.project_manager -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, ProjectManagerInputActivity::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * this function will set items in the navigation view and calls another function for setting the item according to role
     * if there is no image at the particular url provided by google than we will set a dummy imgae
     * else we set the image provided by google and set the name according to the google display name
     */
    @SuppressLint("SetTextI18n")
    fun setNavigationViewItem() {
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        nav_view.setNavigationItemSelectedListener(this)
        val viewH = nav_view.getHeaderView(0)
        val acct = GoogleSignIn.getLastSignedInAccount(this@UserBookingsDashboardActivity)
        viewH.nv_profile_name.text = "Hello, ${acct!!.displayName}"
        val personPhoto = acct.photoUrl
        viewH.nv_profile_email.text = acct.email
        if (personPhoto == null) {
            viewH.nv_profile_image.setImageResource(R.drawable.cat)
        } else {
            Glide.with(applicationContext).load(personPhoto).thumbnail(1.0f).into(viewH.nv_profile_image)
        }
        setItemInDrawerByRole()
    }

    /**
     * this function will set item in navigation drawer according to the data stored in the sharedpreference
     * if the code is 11 than the role is HR
     * if the code is 12 than the role is Project manager
     * else the role is normal user
     */
    private fun setItemInDrawerByRole() {
        val pref = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
        val code = pref.getInt("Code", 10)
        if (code != 11) {
            val navMenu = nav_view.menu
            navMenu.findItem(R.id.HR).isVisible = false
        }
        if (code != 12) {
            val navMenu = nav_view.menu
            navMenu.findItem(R.id.project_manager).isVisible = false
        }
    }

    /**
     * this function gets the data from Backend
     * if there is no data from backend than it will set view to empty
     * else it will call another function for filtering the data
     */
    private fun loadDashboard() {
        val acct = GoogleSignIn.getLastSignedInAccount(application)
        val email = acct!!.email.toString()
        mBookingDashboardViewModel.getBookingList(this, email).observe(this, Observer {
            if (it.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                Glide.with(this).load(R.drawable.yoga_lady_croped).into(empty_view)
                r1_dashboard.setBackgroundColor(Color.parseColor("#FFFFFF"))
            } else {
                empty_view.visibility = View.GONE
            }
            setFilteredDataToAdapter(it)
        })
    }

    /**
     * on Restart of activity it will call a function which will get the updated data from backend
     */
    override fun onRestart() {
        super.onRestart()
        val acct = GoogleSignIn.getLastSignedInAccount(application)
        mBookingDashboardViewModel.mBookingDashboardRepository!!.makeApiCall(this, acct!!.email.toString())
    }

    /**
     * this function will call a function which will filter the data after that set the filtered data to adapter
     */
    private fun setFilteredDataToAdapter(dashboardItemList: List<Dashboard>) {
        finalList.clear()
        getFilteredList(dashboardItemList)
        dashBord_recyclerView1.adapter =
            DashBoardAdapter(
                finalList,
                this@UserBookingsDashboardActivity,
                object : DashBoardAdapter.DashBoardInterface {
                    override fun onCancelClicked() {
                        //getUpdatedDataFromApi()
                        // loadDashboard()
                    }
                })
    }

    /**
     * perform filter task on List of all booking whether they are of recurring type of not
     */
    private fun getFilteredList(dashboardItemList: List<Dashboard>) {
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
                val final = Manager()
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
                finalList.add(final)
            }
        }
    }

    private fun signOut() {
        mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                Toast.makeText(applicationContext, getString(R.string.sign_out), Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
//        val pref = getSharedPreferences(getString(R.string.preference), Context.MODE_PRIVATE)
//        pref.edit().putInt(Constants.EXTRA_REGISTERED, 0)
//        pref.edit().apply()
    }
}


/**
 * get the updated data from backend
 */
//fun getUpdatedDataFromApi() {
//    mBookingDashboardViewModel.getBookingList(this,"prateek300patel@gmail.com")
//        mBookingDashboardViewModel.getBookingList(this, "prateek300patel@gmail.com").observe(this, Observer {
//            if(it.isEmpty()) {
//                empty_view.visibility = View.VISIBLE
//                r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
//            } else {
//                empty_view.visibility = View.GONE
//                setFilteredDataToAdapter(it)
//            }
//        })
//}
