package com.example.conferencerommapp.Helper

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Activity.UpdateBookingActivity
import com.example.conferencerommapp.Model.GetIntentDataFromActvity
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R

@Suppress("NAME_SHADOWING")
class DashBoardAdapter(
    private val dashboardItemList: ArrayList<Manager>,
    val mContext: Context,
    private val btnListener: DashBoardAdapter.CancelBtnClickListener,
    private val mShowMembers: DashBoardAdapter.ShowMembersListener,
    private val mShowDates: DashBoardAdapter.ShowDatesForRecurringMeeting,
    private val mEditBooking: DashBoardAdapter.EditBookingListener
) : androidx.recyclerview.widget.RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    /**
     * a variable which will hold the 'Instance' of interface
     */
    companion object {
        var mCancelBookingClickListener: CancelBtnClickListener? = null
        var mShowMembersListener: ShowMembersListener? = null
        var mShowDateListener: ShowDatesForRecurringMeeting? = null
        var mEditBookingListener: EditBookingListener? = null
    }

    private var currentPosition = 0

    /**
     * this override function will set a view for the recyclerview items
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_list, parent, false)
        return ViewHolder(view)
    }

    /**
     * bind data with the view
     */
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mCancelBookingClickListener = btnListener
        mShowMembersListener = mShowMembers
        mShowDateListener = mShowDates
        mEditBookingListener = mEditBooking

        val fromTime = dashboardItemList[position].FromTime
        val toTime = dashboardItemList[position].ToTime
        val fromDate = fromTime!!.split("T")
        val toDate = toTime!!.split("T")

        setAnimationToTheRecyclerViewItem(holder, position)
        setDataToFields(holder, position)

        holder.fromTimeTextView.text = fromDate[1] + " - " + toDate[1]
        setButtonFunctionalityAccordingToStatus(holder, position)
        setFunctionOnButton(holder, position, mContext)

        holder.updateTextView.setOnClickListener {
            editActivity(position, mContext)
        }
    }


    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return dashboardItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this, itemView)
        }

        @Nullable
        @BindView(R.id.building_name)
        lateinit var buildingNameTextView: TextView
        @BindView(R.id.conferenceRoomName)
        lateinit var roomNameTextView: TextView
        @Nullable
        @BindView(R.id.from_time)
        lateinit var fromTimeTextView: TextView
        @BindView(R.id.date)
        lateinit var dateTextView: TextView
        @Nullable
        @BindView(R.id.purpose)
        lateinit var purposeTextView: TextView
        @BindView(R.id.btnCancel)
        lateinit var cancelButton: Button
        @BindView(R.id.linearlayout)
        lateinit var linearLayout: LinearLayout
        @BindView(R.id.card)
        lateinit var card: CardView
        @BindView(R.id.btnshow)
        lateinit var showButton: Button
        @BindView(R.id.edit_time)
        lateinit var updateTextView: TextView
        var dashboard: Manager? = null
    }

    /**
     * function for setting Animation for RecyclerView item
     */
    private fun setAnimationToTheRecyclerViewItem(holder: ViewHolder, position: Int) {
        holder.card.setOnClickListener {
            currentPosition = position
            notifyDataSetChanged()
        }
        if (currentPosition == position) {
            if (holder.linearLayout.visibility == View.GONE) {
                val mAnimation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.animation)
                holder.linearLayout.visibility = View.VISIBLE
                holder.linearLayout.startAnimation(mAnimation)
            } else {
                val mAnimation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
                holder.linearLayout.visibility = View.GONE
                holder.linearLayout.startAnimation(mAnimation)
            }

        } else {
            val mAnimation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
            holder.linearLayout.visibility = View.GONE
            holder.linearLayout.startAnimation(mAnimation)
        }
    }

    /**
     * set data to the fields of view
     */
    private fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.dashboard = dashboardItemList[position]
        holder.buildingNameTextView.text = dashboardItemList[position].BName
        holder.roomNameTextView.text = dashboardItemList[position].CName
        holder.purposeTextView.text = dashboardItemList[position].Purpose
        holder.showButton.setOnClickListener {
            setMeetingMembers(position)
        }
    }

    /**
     * send employee List to the activity using interface which will display the list of employee names in the alert dialog
     */
    private fun setMeetingMembers(position: Int) {
        mShowMembersListener!!.showMembers(dashboardItemList[position].Name!!)
    }

    /**
     * set button text according to the type of meeting and booking status
     */
    private fun setButtonFunctionalityAccordingToStatus(holder: ViewHolder, position: Int) {
        if (dashboardItemList[position].fromlist.size == 1)
            holder.dateTextView.text = dashboardItemList[position].FromTime!!.split("T")[0]
        else {
            setDataToDialogShowDates(holder, position, mContext)
        }
    }

    /**
     * if the meeting is recurring then add dates of meeting to the data field
     */
    @SuppressLint("SimpleDateFormat")

    private fun setDataToDialogShowDates(
        holder: ViewHolder,
        position: Int,
        context: Context
    ) {
        holder.dateTextView.text = context.getString(R.string.show_dates)
        holder.dateTextView.setTextColor(Color.parseColor("#0072BC"))
        holder.cancelButton.visibility = View.INVISIBLE
        holder.dateTextView.setOnClickListener {
            mShowDates.showDates(position)
        }
    }


    /**
     * if the booking is cancelled by HR than do nothing and set clickable property to false
     * if the booking is not cancelled and user wants to cancel it than allow user to cancel the booking
     */
    private fun setFunctionOnButton(
        holder: ViewHolder,
        position: Int,
        context: Context
    ) {
        if (dashboardItemList[position].Status[0].trim() == "Cancelled") {
            holder.cancelButton.text = context.getString(R.string.cancelled)
            holder.cancelButton.isEnabled = false
        } else {
            holder.cancelButton.text = context.getString(R.string.cancel)
            holder.cancelButton.isEnabled = true
            holder.cancelButton.setOnClickListener {
                mCancelBookingClickListener!!.onCLick(position)
            }
        }
    }

    private fun editActivity(position: Int, mContext: Context) {
        val mGetIntentDataFromActivity = GetIntentDataFromActvity()
        val fromTime = dashboardItemList[position].FromTime
        val fromDate = fromTime!!.split("T")
        mGetIntentDataFromActivity.purpose = dashboardItemList[position].Purpose
        mGetIntentDataFromActivity.buildingName = dashboardItemList[position].BName
        mGetIntentDataFromActivity.roomName = dashboardItemList[position].CName
        mGetIntentDataFromActivity.roomId = dashboardItemList[position].CId.toString()
        mGetIntentDataFromActivity.date = fromDate[0]
        mGetIntentDataFromActivity.fromTime = dashboardItemList[position].FromTime
        mGetIntentDataFromActivity.toTime = dashboardItemList[position].ToTime
        mGetIntentDataFromActivity.cCMail = dashboardItemList[position].cCMail
        mEditBookingListener!!.editBooking(mGetIntentDataFromActivity)
//        val updateActivity = Intent(mContext, UpdateBookingActivity::class.java)
//        updateActivity.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActivity)
//        mContext.startActivity(updateActivity)
    }

    private fun editAlert(position: Int, context: Context) {
        val mGetIntentDataFromActvity = GetIntentDataFromActvity()
        val fromTime = dashboardItemList[position].FromTime
        val fromDate = fromTime!!.split("T")
        mGetIntentDataFromActvity.purpose = dashboardItemList[position].Purpose
        mGetIntentDataFromActvity.buildingName = dashboardItemList[position].BName
        mGetIntentDataFromActvity.roomName = dashboardItemList[position].CName
        mGetIntentDataFromActvity.roomId = dashboardItemList[position].CId.toString()
        mGetIntentDataFromActvity.date = fromDate[0]
        mGetIntentDataFromActvity.fromTime = dashboardItemList[position].FromTime
        mGetIntentDataFromActvity.toTime = dashboardItemList[position].ToTime
        mGetIntentDataFromActvity.cCMail = dashboardItemList[position].cCMail
        //mEditBookingListener!!.editBooking(position)
//        val updateActivity = Intent(mContext, UpdateBookingActivity::class.java)
//        updateActivity.putExtra(Constants.EXTRA_INTENT_DATA, mGetIntentDataFromActvity)
//        mContext.startActivity(updateActivity)
    }

    /**
     * An interface which will be implemented by UserDashboardBookingActivity activity
     */
    interface CancelBtnClickListener {
        fun onCLick(position: Int)
    }

    /**
     * An interface which will be implemented by UserDashboardBookingActivity activity to pass employeeList to the activity
     */
    interface ShowMembersListener {
        fun showMembers(mEmployeeList: List<String>)
    }


    /**
     * An interface which will be implemented by UserDashboardBookingActivity activity to pass position of item which user wants to delete to the activity
     */
    interface ShowDatesForRecurringMeeting {
        fun showDates(position: Int)
    }


    interface EditBookingListener {
        fun editBooking(mGetIntentDataFromActvity: GetIntentDataFromActvity)
    }
}
