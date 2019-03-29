package com.example.conferencerommapp.Helper

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
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
import com.example.conferencerommapp.Activity.UserBookingsDashboardActivity
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.CancelBookingViewModel

class DashBoardAdapter(val dashboardItemList: ArrayList<Manager>,val mContext: Context,
                       val dashBoardInterface: DashBoardInterface) :androidx.recyclerview.widget.RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    var currentPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setAnimationToTheRecyclerViewItem(holder, position)

        setDataToFields(holder, position)

        var fromtime = dashboardItemList[position].FromTime
        var totime = dashboardItemList[position].ToTime
        var datefrom = fromtime!!.split("T")
        var dateto = totime!!.split("T")

        holder.fromtimetextview.text = datefrom.get(1) + " - " + dateto.get(1)

        setButtonFunctionalityAccordingToStatus(holder, position)

        setFunctionOnButton(holder, position)
    }

    /**
     * A function for cancel a booking
     */
    private fun cancelBooking(mCancel: CancelBooking, mContext: Context) {

        /**
         * setting the observer for making the api call for cancelling the booking
         */
        var mCancelBookingViewModel =
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
        val buildingNameTextview: TextView = itemView.findViewById(R.id.building_name)
        val roomNameTextview: TextView = itemView.findViewById(R.id.conferenceRoomName)
        val fromtimetextview: TextView = itemView.findViewById(R.id.from_time)
        val dateTextview: TextView = itemView.findViewById(R.id.date)
        val purposeTextview: TextView = itemView.findViewById(R.id.purpose)
        val cancelButton: Button = itemView.findViewById(R.id.btnCancel)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearlayout)
        val card: CardView = itemView.findViewById(R.id.card)
        val showButton: Button = itemView.findViewById(R.id.btnshow)
        var dashboard: Manager? = null
    }

    /**
     * function for setting Animation for RecyclerView item
     */
    fun setAnimationToTheRecyclerViewItem(holder: ViewHolder, position: Int) {
        holder.card.setOnClickListener(View.OnClickListener {
            currentPosition = position
            notifyDataSetChanged()

        })
        if (currentPosition == position) {
            if (holder.linearLayout.visibility == View.GONE) {
                var animmation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.animation)
                holder.linearLayout.visibility = View.VISIBLE
                holder.linearLayout.startAnimation(animmation)
            } else {
                var animation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
                holder.linearLayout.visibility = View.GONE
                holder.linearLayout.startAnimation(animation)
            }

        } else {
            var animation: Animation = AnimationUtils.loadAnimation(mContext, R.anim.close)
            holder.linearLayout.visibility = View.GONE
            holder.linearLayout.startAnimation(animation)
        }
    }

    /**
     * set data to the fields of view
     */
    fun setDataToFields(holder: ViewHolder, position: Int) {
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
    fun setMeetingMembers(position: Int) {
        var list = dashboardItemList[position].Name
        var arrayList = ArrayList<String>()
        for (item in list!!) {
            arrayList.add(item)
        }
        var listItems = arrayOfNulls<String>(arrayList.size)
        arrayList.toArray(listItems)
        val builder = android.app.AlertDialog.Builder(mContext)
        builder.setTitle("Members of meeting.")
        builder.setItems(listItems, DialogInterface.OnClickListener { dialog, which -> }
        )
        val mDialog = builder.create()
        mDialog.show()
    }

    /**
     * set button text according to the type of meeting and booking status
     */
    fun setButtonFunctionalityAccordingToStatus(holder: ViewHolder, position: Int) {

        if (dashboardItemList[position].fromlist.size == 1)
            holder.dateTextview.text = dashboardItemList[position].FromTime!!.split("T").get(0)
        else {
            setDataToDialogShowDates(holder, position)
        }
    }

    /**
     * if the meeting is recurring then add dates of meeting to the data field
     */
    fun setDataToDialogShowDates(holder: ViewHolder, position: Int) {

        holder.dateTextview.text = "Show Dates"
        holder.dateTextview.setTextColor(Color.parseColor("#0072BC"))
        holder.cancelButton.visibility = View.INVISIBLE
        holder.dateTextview.setOnClickListener {
            var list = dashboardItemList[position].fromlist
            var arrayList = ArrayList<String>()
            for (item in list) {

                arrayList.add(item.split("T")[0])
            }
            var listItems = arrayOfNulls<String>(arrayList.size)
            arrayList.toArray(listItems)
            val builder = android.app.AlertDialog.Builder(mContext)
            builder.setTitle("Dates Of Meeting.")
            builder.setItems(listItems, DialogInterface.OnClickListener { dialog, which ->
                if (dashboardItemList[position].Status[which] == "Cancelled") {
                    Toast.makeText(mContext, "Cancelled by HR! Please check your email", Toast.LENGTH_LONG).show()
                } else {
                    var builder = GetAleretDialog.getDialog(mContext, "Confirm", "Press ok to Cancel the booking for the date '${listItems.get(which).toString()}'")
                    builder.setPositiveButton(mContext.getString(R.string.ok)) { dialog, postion ->
                        var mCancel = CancelBooking()
                        mCancel.roomId = dashboardItemList[position].CId
                        mCancel.email = dashboardItemList[position].Email
                        mCancel.fromTime =
                            listItems.get(which).toString() + "T" + dashboardItemList[position].FromTime!!.split("T")[1]
                        mCancel.toTime =
                            listItems.get(which).toString() + "T" + dashboardItemList[position].ToTime!!.split("T")[1]
                        cancelBooking(mCancel, mContext)
                    }
                    builder.setNegativeButton(mContext.getString(R.string.cancel)) { dialog, postion ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.setCancelable(false)
                    dialog.show()
                    ColorOfDialogButton.setColorOfDialogButton(dialog)
                }

            })
            val mDialog = builder.create()
            mDialog.show()
        }
    }

    /**
     * if the booking is cancelled by HR than do nothing and set clickable property to false
     * if the booking is not cancelled and user wants to cancel it than allow user to cancel the booking
     */
    fun setFunctionOnButton(holder: ViewHolder, position: Int) {
        if (dashboardItemList[position].Status[0].trim() == "Cancelled") {
            holder.cancelButton.text = "Cancelled"
            holder.cancelButton.isEnabled = false
        } else {
            holder.cancelButton.text = "Cancel"
            holder.cancelButton.isEnabled = true
            holder.cancelButton.setOnClickListener {
                var mBuilder = GetAleretDialog.getDialog(mContext, "Confirm", "Are you sure you want to cancel the meeting?")
                mBuilder.setPositiveButton(mContext.getString(R.string.yes)) { dialog, which ->
                    var cancel = CancelBooking()
                    cancel.roomId = dashboardItemList.get(position).CId
                    cancel.toTime = dashboardItemList[position].ToTime
                    cancel.fromTime = dashboardItemList[position].FromTime
                    cancel.email = dashboardItemList.get(position).Email
                    cancelBooking(cancel, mContext)
                }
                mBuilder.setNegativeButton(mContext.getString(R.string.no)) { dialog, which ->
                }
                val dialog = GetAleretDialog.showDialog(mBuilder)
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        }
    }

    interface DashBoardInterface {
        fun onCancelClicked()
    }

}