package com.example.conferencerommapp.Helper

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.CancelBookingViewModel

@Suppress("NAME_SHADOWING")
class DashBoardAdapter(
    private val dashboardItemList: ArrayList<Manager>,
    val mContext: Context
) : androidx.recyclerview.widget.RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    private var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_list, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setAnimationToTheRecyclerViewItem(holder, position)

        setDataToFields(holder, position)

        val fromtime = dashboardItemList[position].FromTime
        val totime = dashboardItemList[position].ToTime
        val datefrom = fromtime!!.split("T")
        val dateto = totime!!.split("T")

        holder.fromtimetextview.text = datefrom[1] + " - " + dateto[1]

        setButtonFunctionalityAccordingToStatus(holder, position)

        setFunctionOnButton(holder, position, mContext)
    }

    /**
     * A function for cancel a booking
     */
    private fun cancelBooking(mCancel: CancelBooking, mContext: Context) {

        /**
         * setting the observer for making the api call for cancelling the booking
         */
        val mCancelBookingViewModel =
            ViewModelProviders.of(mContext as UserBookingsDashboardActivity).get(CancelBookingViewModel::class.java)
        mCancelBookingViewModel.cancelBooking(mContext, mCancel).observe(mContext, Observer {
            Toast.makeText(mContext, "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show()
            (mContext).mBookingDashboardViewModel.mBookingDashboardRepository!!.makeApiCall(
                mContext,
                mCancel.email!!
            )

            //dashBoardInterface.onCancelClicked()
//            }else {
//                Toast.makeText(mContext, "Response Error", Toast.LENGTH_LONG).show()
//            }
        })
    }

    /**
     * it will return number of items contains in recyclerview view
     */
    override fun getItemCount(): Int {
        return dashboardItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this,itemView)
        }

        @BindView(R.id.building_name)
        lateinit var buildingNameTextview: TextView
        @BindView(R.id.conferenceRoomName)
        lateinit var roomNameTextview: TextView
        @BindView(R.id.from_time)
        lateinit var fromtimetextview: TextView
        @BindView(R.id.date)
        lateinit var dateTextview: TextView
        @BindView(R.id.purpose)
        lateinit var purposeTextview: TextView
        @BindView(R.id.btnCancel)
        lateinit var cancelButton: Button
        @BindView(R.id.linearlayout)
        lateinit var linearLayout: LinearLayout
        @BindView(R.id.card)
        lateinit var card: CardView
        @BindView(R.id.btnshow)
        lateinit var showButton: Button
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
                val animmation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.animation)
                holder.linearLayout.visibility = View.VISIBLE
                holder.linearLayout.startAnimation(animmation)
            } else {
                val animation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
                holder.linearLayout.visibility = View.GONE
                holder.linearLayout.startAnimation(animation)
            }

        } else {
            val animation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
            holder.linearLayout.visibility = View.GONE
            holder.linearLayout.startAnimation(animation)
        }
    }

    /**
     * set data to the fields of view
     */
    private fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.dashboard = dashboardItemList[position]
        holder.buildingNameTextview.text = dashboardItemList[position].BName
        holder.roomNameTextview.text = dashboardItemList[position].CName
        holder.purposeTextview.text = dashboardItemList[position].Purpose
        holder.showButton.setOnClickListener {
            setMeetingMembers(position)
        }
    }

    /**
     * set data for the dialog items
     */
    private fun setMeetingMembers(position: Int) {
        val list = dashboardItemList[position].Name
        val arrayList = ArrayList<String>()
        for (item in list!!) {
            arrayList.add(item)
        }
        val listItems = arrayOfNulls<String>(arrayList.size)
        arrayList.toArray(listItems)
        val builder = android.app.AlertDialog.Builder(mContext)
        builder.setTitle("Members of meeting.")
        builder.setItems(listItems
        ) { _, _ -> }
        val mDialog = builder.create()
        mDialog.show()
    }

    /**
     * set button text according to the type of meeting and booking status
     */
    private fun setButtonFunctionalityAccordingToStatus(holder: ViewHolder, position: Int) {

        if (dashboardItemList[position].fromlist.size == 1)
            holder.dateTextview.text = dashboardItemList[position].FromTime!!.split("T")[0]
        else {
            setDataToDialogShowDates(holder, position, mContext)
        }
    }

    /**
     * if the meeting is recurring then add dates of meeting to the data field
     */
    private fun setDataToDialogShowDates(
        holder: ViewHolder,
        position: Int,
        context: Context
    ) {

        holder.dateTextview.text = context.getString(R.string.show_dates)
        holder.dateTextview.setTextColor(Color.parseColor("#0072BC"))
        holder.cancelButton.visibility = View.INVISIBLE
        holder.dateTextview.setOnClickListener {
            val list = dashboardItemList[position].fromlist
            val arrayList = ArrayList<String>()
            for (item in list) {
                arrayList.add(item.split("T")[0])
            }
            val listItems = arrayOfNulls<String>(arrayList.size)
            arrayList.toArray(listItems)
            val builder = android.app.AlertDialog.Builder(mContext)
            builder.setTitle(context.getString(R.string.dates_of_meeting))
            builder.setItems(listItems) { _, which ->
                if (dashboardItemList[position].Status[which] == "Cancelled") {
                    Toast.makeText(mContext, context.getString(R.string.cancelled_by_hr), Toast.LENGTH_LONG).show()
                } else {
                    val builder = GetAleretDialog.getDialog(
                        mContext,
                        context.getString(R.string.confirm),
                        "Press ok to Cancel the booking for the date '${listItems.get(index = which).toString()}'"
                    )
                    builder.setPositiveButton(mContext.getString(R.string.ok)) { _, _ ->
                        val mCancel = CancelBooking()
                        mCancel.roomId = dashboardItemList[position].CId
                        mCancel.email = dashboardItemList[position].Email
                        mCancel.fromTime =
                            listItems[which].toString() + "T" + dashboardItemList[position].FromTime!!.split("T")[1]
                        mCancel.toTime =
                            listItems[which].toString() + "T" + dashboardItemList[position].ToTime!!.split("T")[1]
                        cancelBooking(mCancel, mContext)
                    }
                    builder.setNegativeButton(mContext.getString(R.string.cancel)) { _, _ ->
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
    }

    /**
     * if the booking is cancelled by HR than do nothing and set clickable property to false
     * if the booking is not cancelled and user wants to cancel it than allow user to cancel the booking
     */
    fun setFunctionOnButton(
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
                val mBuilder =
                    GetAleretDialog.getDialog(mContext, "Confirm", "Are you sure you want to cancel the meeting?")
                mBuilder.setPositiveButton(mContext.getString(R.string.yes)) { _, _ ->
                    val cancel = CancelBooking()
                    cancel.roomId = dashboardItemList[position].CId
                    cancel.toTime = dashboardItemList[position].ToTime
                    cancel.fromTime = dashboardItemList[position].FromTime
                    cancel.email = dashboardItemList[position].Email
                    cancelBooking(cancel, mContext)
                }
                mBuilder.setNegativeButton(mContext.getString(R.string.no)) { _, _ ->
                }
                val dialog = GetAleretDialog.showDialog(mBuilder)
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        }
    }


}