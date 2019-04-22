package com.example.conferencerommapp.Activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
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
import com.example.conferencerommapp.Helper.*
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Dashboard
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.SignIn
import com.example.conferencerommapp.ViewModel.BookingDashboardViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.app_bar_main2.*
import kotlinx.android.synthetic.main.content_main2.*
import kotlinx.android.synthetic.main.nav_header_main2.view.*
import java.text.SimpleDateFormat

@Suppress("DEPRECATION")
class UserBookingsDashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var finalList = ArrayList<Manager>()
    private lateinit var mBookingDashBoardViewModel: BookingDashboardViewModel
    private lateinit var acct: GoogleSignInAccount
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        ButterKnife.bind(this)
        setNavigationViewItem()
        progressDialog = GetProgress.getProgressDialog(getString(R.string.progress_message), this)
        acct = GoogleSignIn.getLastSignedInAccount(application)!!
        mBookingDashBoardViewModel = ViewModelProviders.of(this).get(BookingDashboardViewModel::class.java)
        loadDashboard()
        observeData()
    }

    /**
     * all observer for LiveData
     */
    private fun observeData() {
        /**
         * observing data for cancel booking
         */
        mBookingDashBoardViewModel.returnBookingCancelled().observe(this, Observer {
            progressDialog.dismiss()
            Toasty.success(this, getString(R.string.cancelled_successful), Toast.LENGTH_SHORT, true).show()
            // make api call to get the updated list of booking after cancellation
            mBookingDashBoardViewModel.getBookingList(acct.email.toString())
        })

        mBookingDashBoardViewModel.returnCancelFailed().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })
        /**
         * observing data for booking list
         */
        mBookingDashBoardViewModel.returnSuccess().observe(this, Observer {
            progressDialog.dismiss()

            if (it.isEmpty()) {
                empty_view.visibility = View.VISIBLE
                //Glide.with(this).load(R.drawable.yoga_lady_croped).into(empty_view)
                dashBord_recyclerView1.visibility = View.GONE
                r1_dashboard.setBackgroundColor(Color.parseColor("#F7F7F7"))
            }

            else {
                empty_view.visibility = View.GONE
                //textView_no_events.visibility = View.GONE
                dashBord_recyclerView1.visibility = View.VISIBLE
            }
            setFilteredDataToAdapter(it)
        })
        mBookingDashBoardViewModel.returnFailure().observe(this, Observer {
            progressDialog.dismiss()
            ShowToast.show(this, it)
        })
    }

    @OnClick(R.id.user_input)
    fun userInputActivityFloatingActionButton() {
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
            R.id.hr_add -> {
                startActivity(Intent(this@UserBookingsDashboardActivity, BuildingDashboard::class.java))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * this function will set items in the navigation view and calls another function for setting the item according to role
     * if there is no image at the particular url provided by google than we will set a dummy imgae
     * else we set the image provided by google and set the employeeList according to the google display employeeList
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
            navMenu.findItem(R.id.hr_add).isVisible = false
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
        val email = acct.email.toString()
        progressDialog.show()
        mBookingDashBoardViewModel.getBookingList(email)
    }

    /**
     * on Restart of activity it will call a function which will get the updated data from backend
     */
    override fun onRestart() {
        super.onRestart()
        val acct = GoogleSignIn.getLastSignedInAccount(application)
        mBookingDashBoardViewModel.getBookingList(acct!!.email.toString())
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
                object : DashBoardAdapter.CancelBtnClickListener {
                    override fun onCLick(position: Int) {
                        showConfirmDialogForCancelMeeting(position)
                    }
                },
                object : DashBoardAdapter.ShowMembersListener {
                    override fun showMembers(mEmployeeList: List<String>) {
                        showMeetingMembers(mEmployeeList)
                    }

                },
                object : DashBoardAdapter.ShowDatesForRecurringMeeting {
                    override fun showDates(position: Int) {
                        setDataToAlertDialogForShowDates(position)
                    }

                },
                object : DashBoardAdapter.EditBookingListener {
                    override fun editBooking(mGetIntentDataFromActvity: GetIntentDataFromActvity) {
                        intentToUpdateBookingActivity(mGetIntentDataFromActvity)
                    }

                }
            )
    }

    /**
     * function will send intent to the UpdateActivity with data which is required for updation
     */
    fun intentToUpdateBookingActivity(mGetIntentDataFromActivity: GetIntentDataFromActvity) {
        val updateActivityIntent = Intent(this, UpdateBookingActivity::class.java)
        updateActivityIntent.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActivity)
        startActivity(updateActivityIntent)
    }

    /**
     * show a dialog to confirm cancel of booking
     * if ok button is pressed than cancelBooking function is called
     */
    fun showConfirmDialogForCancelMeeting(position: Int) {
        val mBuilder =
            GetAleretDialog.getDialog(
                this@UserBookingsDashboardActivity,
                getString(R.string.cancel),
                getString(R.string.sure_cancel_meeting)
            )
        mBuilder.setPositiveButton(getString(R.string.yes)) { _, _ ->
            /**
             * object which is required for the API call
             */
            val mCancel = CancelBooking()
            mCancel.roomId = finalList[position].CId
            mCancel.toTime = finalList[position].ToTime
            mCancel.fromTime = finalList[position].FromTime
            mCancel.email = finalList[position].Email
            cancelBooking(mCancel)
        }
        mBuilder.setNegativeButton(getString(R.string.no)) { _, _ ->
        }
        val dialog = GetAleretDialog.showDialog(mBuilder)
        ColorOfDialogButton.setColorOfDialogButton(dialog)
    }

    fun setDataToAlertDialogForShowDates(position: Int) {
        val list = finalList[position].fromlist
        val arrayList = ArrayList<String>()
        for (item in list) {
            arrayList.add(formatDate(item))
        }
        val listItems = arrayOfNulls<String>(arrayList.size)
        arrayList.toArray(listItems)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dates_of_meeting))
        builder.setItems(listItems) { _, which ->
            if (finalList[position].Status[which] == "Cancelled") {
                Toasty.info(this, getString(R.string.cancelled_by_hr),Toast.LENGTH_SHORT, true).show()
            } else {
                val builder = GetAleretDialog.getDialog(
                    this,
                    getString(R.string.cancel),
                    "Press ok to Cancel the booking for the date '${listItems.get(index = which).toString()}'"
                )
                builder.setPositiveButton(getString(R.string.ok)) { _, _ ->
                    val mCancel = CancelBooking()
                    val date = reverseDateFormat(listItems[which].toString())
                    mCancel.roomId = finalList[position].CId
                    mCancel.email = finalList[position].Email
                    mCancel.fromTime =
                        date + "T" + finalList[position].FromTime!!.split("T")[1]
                    mCancel.toTime =
                        date + "T" + finalList[position].ToTime!!.split("T")[1]
                    cancelBooking(mCancel)
                }
                builder.setNegativeButton(getString(R.string.cancel)) { _, _ ->
                }
                //Setting the Uodate Options
                builder.setNeutralButton("Update") { _, _ ->

                  updateRecurringBookings(finalList,position)
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCancelable(false)
                dialog.show()
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }

        }
        val mDialog = builder.create()
        mDialog.show()
    }

    // Passing Intent to the UpdateBooking Activity
    private fun updateRecurringBookings(finalList: ArrayList<Manager>, position: Int) {
        val mGetIntentDataFromActvity = GetIntentDataFromActvity()
        val fromTime = finalList[position].FromTime
        val dateFrom = fromTime!!.split("T")
        mGetIntentDataFromActvity.purpose = finalList[position].Purpose
        mGetIntentDataFromActvity.buildingName = finalList[position].BName
        mGetIntentDataFromActvity.roomName = finalList[position].CName
        mGetIntentDataFromActvity.date = dateFrom[0]
        mGetIntentDataFromActvity.roomId = finalList[position].CId.toString()
        mGetIntentDataFromActvity.fromTime = finalList[position].FromTime
        mGetIntentDataFromActvity.toTime = finalList[position].ToTime
        mGetIntentDataFromActvity.cCMail = finalList[position].cCMail
        val updateBookingActivityButton =Intent(this,UpdateBookingActivity::class.java)
        updateBookingActivityButton.putExtra(Constants.EXTRA_INTENT_DATA,mGetIntentDataFromActvity)
        startActivity(updateBookingActivityButton)
    }

    /**
     * format date from yyyy-MM-dd to dd-MM-yyyy
     */
    @SuppressLint("SimpleDateFormat")
    private fun formatDate(date: String): String {
        val simpleDateFormatInput = SimpleDateFormat(getString(R.string.input_date_format_for_booking_dashboard))
        val simpleDateFormatOutput = SimpleDateFormat(getString(R.string.ount_date_format_for_booking_dashboard))
        return simpleDateFormatOutput.format(simpleDateFormatInput.parse(date.split("T")[0]))
    }

    /**
     * format date from dd-MM-yyyy to yyyy-MM-dd
     */
    @SuppressLint("SimpleDateFormat")
    private fun reverseDateFormat(date: String): String {
        val simpleDateFormatInput = SimpleDateFormat("yyyy-MM-dd")
        val simpleDateFormatOutput = SimpleDateFormat("dd MMMM yyyy")
        return simpleDateFormatInput.format(simpleDateFormatOutput.parse(date))
    }


    /**
     * Display the list of employee names in the alert dialog
     */
    fun showMeetingMembers(mEmployeeList: List<String>) {
        val arrayListOfNames = ArrayList<String>()
        for (item in mEmployeeList!!) {
            arrayListOfNames.add(item)
        }
        val listItems = arrayOfNulls<String>(arrayListOfNames.size)
        arrayListOfNames.toArray(listItems)
        val builder = AlertDialog.Builder(this)
        builder.setItems(
            listItems
        ) { _, _ -> }
        val mDialog = builder.create()
        mDialog.show()
    }

    /**
     * A function for cancel a booking
     */
    private fun cancelBooking(mCancel: CancelBooking) {
        progressDialog.show()
        mBookingDashBoardViewModel.cancelBooking(mCancel)
    }

    /**
     * perform filter task on List of all booking whether they are of recurring type of not
     */
    private fun getFilteredList(dashboardItemList: List<Dashboard>) {
        for (item in dashboardItemList) {
            var flag = 0
            for (i in finalList) {
                if (
                    i.Purpose == item.purpose &&
                    i.FromTime!!.split("T")[1] == item.fromTime!!.split("T")[1] &&
                    i.ToTime!!.split("T")[1] == item.toTime!!.split("T")[1]
                ) {
                    i.fromlist.add(item.fromTime!!)
                    i.Status.add(item.status!!)
                    flag = 1
                    break
                }
            }
            if (flag == 0) {
                val final = Manager()
                final.BName = item.buildingName
                final.CId = item.roomId
                final.CName = item.roomName
                final.Purpose = item.purpose
                final.Name = item.name
                final.FromTime = item.fromTime
                final.ToTime = item.toTime
                final.Email = item.email
                final.Status.add(item.status!!)
                final.cCMail = item.cCMail
                final.fromlist.add(item.fromTime!!.split("T")[0])
                finalList.add(final)
            }
        }
    }

    /**
     * function will sign out the current user and send control to SignInActivity
     */
    private fun signOut() {
        mGoogleSignInClient = GoogleGSO.getGoogleSignInClient(this)
        mGoogleSignInClient!!.signOut()
            .addOnCompleteListener(this) {
                Toast.makeText(applicationContext, getString(R.string.sign_out), Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, SignIn::class.java))
                finish()
            }
    }
}


