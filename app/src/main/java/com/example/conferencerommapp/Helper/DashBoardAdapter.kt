package com.example.conferencerommapp.Helper

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.util.Log
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
import androidx.core.content.ContextCompat.startActivity
import com.example.conferencerommapp.Activity.Main2Activity
import com.example.conferencerommapp.Model.CancelBooking
import com.example.conferencerommapp.Model.Manager
import com.example.conferencerommapp.R
import com.example.conferencerommapp.services.ConferenceService
import com.example.globofly.services.Servicebuilder
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DashBoardAdapter(val dashboardItemList: ArrayList<Manager>, val contex: Context) :
    androidx.recyclerview.widget.RecyclerView.Adapter<DashBoardAdapter.ViewHolder>() {

    var progressDialog: ProgressDialog? = null
    var currentPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
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
        holder.dashboard = dashboardItemList[position]
        holder.txvBName.text = dashboardItemList[position].BName
        holder.txvRoomName.text = dashboardItemList[position].CName
        holder.txvPurpose.text = dashboardItemList[position].Purpose

        var fromtime = dashboardItemList[position].FromTime
        var totime = dashboardItemList[position].ToTime
        var datefrom = fromtime!!.split("T")
        var dateto = totime!!.split("T")


        holder.btnShow.setOnClickListener {
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
        if (dashboardItemList[position].fromlist.size == 1)
            holder.txvDate.text = datefrom.get(0)
        else {
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
                    if(dashboardItemList[position].Status[which] == "Cancelled") {
                        Toast.makeText(contex,"Cancelled by HR! Please check your Email",Toast.LENGTH_LONG).show()
                    }else {
                        var builder = AlertDialog.Builder(contex)
                        builder.setTitle("Confirm ")
                        builder.setMessage("Press ok to Delete the booking for the date '${listItems.get(which).toString()}'")
                        builder.setPositiveButton("Ok") { dialog, postion ->
                            var can = CancelBooking()
                            can.CId = dashboardItemList[position].CId
                            can.Email = dashboardItemList[position].Email
                            can.FromTime = listItems.get(which).toString() + "T" + fromtime!!.split("T")[1]
                            can.ToTime = listItems.get(which).toString() + "T" + totime!!.split("T")[1]
                            cancelBooking(can, contex)
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                        var button_p: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        if (button_p != null) {
                            button_p.setBackgroundColor(Color.WHITE)
                            button_p.setTextColor(Color.parseColor("#0072bc"))
                        }
                    }

                })
                val mDialog = builder.create()
                mDialog.show()
            }
        }
        holder.txvFrom.text = datefrom.get(1) + " - " + dateto.get(1)
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
                var button_p: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
                var button_n: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                if (button_p != null) {
                    button_p.setBackgroundColor(Color.WHITE)
                    button_p.setTextColor(Color.parseColor("#0072bc"))
                }
                if (button_n != null) {

                    button_n.setBackgroundColor(Color.WHITE)
                    button_n.setTextColor(Color.BLACK)
                }
            }
        }
    }

    private fun cancelBooking(cancel: CancelBooking, contex: Context) {
        progressDialog = ProgressDialog(contex)
        progressDialog!!.setMessage("Processing....")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
        val service = Servicebuilder.buildService(ConferenceService::class.java)
        val requestCall: Call<ResponseBody> = service.cancelBooking(cancel)
        requestCall.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressDialog!!.dismiss()
                Toast.makeText(contex, "Error on Failure", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.i("---------response", response.toString())
                progressDialog!!.dismiss()
                if (response.isSuccessful) {
                    val code = response.body()
                    Toast.makeText(contex, "Booking Cancelled Successfully", Toast.LENGTH_SHORT).show()
                    startActivity(contex, Intent(contex, Main2Activity::class.java), null)
                    (contex as Activity).finish()
                } else {
                    Toast.makeText(contex, "Response Error", Toast.LENGTH_LONG).show()
                }

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

}