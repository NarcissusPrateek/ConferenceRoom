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
import com.example.conferencerommapp.Activity.Main2Activity
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.CancelBookingViewModel


class DashBoardAdapter(val dashboardItemList: ArrayList<Manager>, val contex: Context) :

    androidx.recyclerview.widget.RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

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

        holder.txvFrom.text = datefrom.get(1) + " - " + dateto.get(1)

        setButtonFunctionalityAccordingToStatus(holder, position)

        setFunctionOnButton(holder, position)
    }

    private fun cancelBooking(mCancel: CancelBooking, context: Context) {

        //setting the observer for making the api call for cancelling the booking

        var mCancelBookingViewModel = ViewModelProviders.of(context as Main2Activity).get(CancelBookingViewModel::class.java)
        mCancelBookingViewModel.cancelBooking(context, mCancel).observe(context, Observer {
            if(it == 200) {
                context.mBookingDashboardViewModel.getBookingList(context, mCancel.Email.toString())
            }else {
                Toast.makeText(context, "Response Error", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun getItemCount(): Int {
        return dashboardItemList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val txvBName: TextView = itemView.findViewById(R.id.building_name)
        val txvRoomName: TextView = itemView.findViewById(R.id.conferenceRoomName)
        val txvFrom: TextView = itemView.findViewById(R.id.from_time)
        val txvDate: TextView = itemView.findViewById(R.id.date)
        val txvPurpose: TextView = itemView.findViewById(R.id.purpose)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearlayout)
        val card: CardView = itemView.findViewById(R.id.card)
        val btnShow: Button = itemView.findViewById(R.id.btnshow)
        var dashboard: Manager? = null
    }

    fun setAnimationToTheRecyclerViewItem(holder: ViewHolder, position: Int) {
        holder.card.setOnClickListener(View.OnClickListener {
            currentPosition = position
            notifyDataSetChanged()

        })
        if (currentPosition == position) {
            if (holder.linearLayout.visibility == View.GONE) {
                var animmation: Animation = AnimationUtils.loadAnimation(contex, R.anim.animation)
                holder.linearLayout.visibility = View.VISIBLE
                holder.linearLayout.startAnimation(animmation)
            } else {
                var animation: Animation = AnimationUtils.loadAnimation(contex, R.anim.close)
                holder.linearLayout.visibility = View.GONE
                holder.linearLayout.startAnimation(animation)
            }

        } else {
            var animation: Animation = AnimationUtils.loadAnimation(contex, R.anim.close)
            holder.linearLayout.visibility = View.GONE
            holder.linearLayout.startAnimation(animation)
        }
    }

    fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.dashboard = dashboardItemList[position]
        holder.txvBName.text = dashboardItemList[position].BName
        holder.txvRoomName.text = dashboardItemList[position].CName
        holder.txvPurpose.text = dashboardItemList[position].Purpose
        holder.btnShow.setOnClickListener {
            setMeetingMembers(position)
        }
    }

    fun setMeetingMembers(position: Int) {
        var list = dashboardItemList[position].Name
        var arrayList = ArrayList<String>()
        for (item in list!!) {
            arrayList.add(item)
        }
        var listItems = arrayOfNulls<String>(arrayList.size)
        arrayList.toArray(listItems)
        val builder = android.app.AlertDialog.Builder(contex)
        builder.setTitle("Members of meeting.")
        builder.setItems(listItems, DialogInterface.OnClickListener { dialog, which -> }
        )
        val mDialog = builder.create()
        mDialog.show()
    }

    fun setButtonFunctionalityAccordingToStatus(holder: ViewHolder, position: Int) {

        if (dashboardItemList[position].fromlist.size == 1)
            holder.txvDate.text = dashboardItemList[position].FromTime!!.split("T").get(0)
        else {
            setDataToDialogShowDates(holder, position)
        }
    }

    fun setDataToDialogShowDates(holder: ViewHolder, position: Int) {

        holder.txvDate.text = "Show Dates"
        holder.txvDate.setTextColor(Color.parseColor("#0072BC"))
        holder.btnCancel.visibility = View.INVISIBLE
        holder.txvDate.setOnClickListener {
            var list = dashboardItemList[position].fromlist
            var arrayList = ArrayList<String>()
            for (item in list!!) {
                arrayList.add(item.split("T")[0])
            }
            var listItems = arrayOfNulls<String>(arrayList.size)
            arrayList.toArray(listItems)
            val builder = android.app.AlertDialog.Builder(contex)
            builder.setTitle("Dates Of Meeting.")
            builder.setItems(listItems, DialogInterface.OnClickListener { dialog, which ->
                if (dashboardItemList[position].Status[which] == "Cancelled") {
                    Toast.makeText(contex, "Cancelled by HR! Please check your Email", Toast.LENGTH_LONG).show()
                } else {
                    //make api call here
                    var builder = AlertDialog.Builder(contex)
                    builder.setTitle("Confirm ")
                    builder.setMessage("Press ok to Delete the mBooking for the date '${listItems.get(which).toString()}'")
                    builder.setPositiveButton("Ok") { dialog, postion ->
                        var can = CancelBooking()
                        can.CId = dashboardItemList[position].CId
                        can.Email = dashboardItemList[position].Email
                        can.FromTime =
                            listItems.get(which).toString() + "T" + dashboardItemList[position].FromTime!!.split("T")[1]
                        can.ToTime =
                            listItems.get(which).toString() + "T" + dashboardItemList[position].FromTime!!.split("T")[1]
                        cancelBooking(can, contex)
                    }
                    builder.setNegativeButton("Cancel") { dialog, postion ->
                    }
                    val dialog: AlertDialog = builder.create()
                    dialog.show()
                    ColorOfDialogButton.setColorOfDialogButton(dialog)
                }

            })
            val mDialog = builder.create()
            mDialog.show()
        }
    }

    fun setFunctionOnButton(holder: ViewHolder, position: Int) {
        if (dashboardItemList[position].Status[0].trim() == "Cancelled") {
            holder.btnCancel.text = "Cancelled"
            holder.btnCancel.isEnabled = false
        } else {
            holder.btnCancel.text = "Cancel"
            holder.btnCancel.isEnabled = true
            holder.btnCancel.setOnClickListener {
                var builder = AlertDialog.Builder(contex)
                builder.setTitle("Confirm ")
                builder.setMessage("Are you sure you want to cancel the meeting?")
                builder.setPositiveButton("YES") { dialog, which ->
                    var cancel = CancelBooking()
                    cancel.CId = dashboardItemList.get(position).CId
                    cancel.ToTime = dashboardItemList[position].ToTime
                    cancel.FromTime = dashboardItemList[position].FromTime
                    cancel.Email = dashboardItemList.get(position).Email
                    //Log.i("---------", cancel.toString())
                    cancelBooking(cancel, contex)
                }
                builder.setNegativeButton("No") { dialog, which ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCancelable(false)
                dialog.show()
                ColorOfDialogButton.setColorOfDialogButton(dialog)
            }
        }
    }
}