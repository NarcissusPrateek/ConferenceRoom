package com.example.conferencerommapp

import android.app.AlertDialog
import android.content.Context
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
import com.example.conferencerommapp.Activity.BlockedDashboard

import com.example.conferencerommapp.Helper.ColorOfDialogButton
import com.example.conferencerommapp.Helper.Unblock
import com.example.conferencerommapp.ViewModel.UnBlockRoomViewModel


class BlockedDashboardNew(private val blockedList: List<Blocked>, val mContext: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<BlockedDashboardNew.ViewHolder>() {

    var currentPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setAnimationToTheRecyclerViewItem(holder, position)
        setDataToFields(holder, position)
        setFunctionOnButton(holder, position)


        holder.itemView.setOnClickListener { v ->
            var id = blockedList[position].CId

        }
    }



    override fun getItemCount(): Int {
        return blockedList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val conferenceName : TextView = itemView.findViewById(R.id.block_conferenceRoomName)
        val buildingName : TextView = itemView.findViewById(R.id.block_building_name)
        val purpose : TextView = itemView.findViewById(R.id.block_purpose)
        val fromtime : TextView = itemView.findViewById(R.id.block_from_time)
        val date : TextView = itemView.findViewById(R.id.block_date)
        val card: CardView = itemView.findViewById(R.id.card_block)
        val linearLayout:LinearLayout = itemView.findViewById(R.id.linearlayout_block)
        val unblock :Button = itemView.findViewById(R.id.unblock)
        var blocked: Blocked? = null
    }

    fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.blocked = blockedList[position]
        holder.conferenceName.text = blockedList[position].CName
        holder.buildingName.text = blockedList[position].BName
        holder.purpose.text = blockedList[position].Purpose
        holder.date.text = blockedList[position].FromTime!!.split("T")[0]
        holder.fromtime.text = blockedList[position].FromTime!!.split("T")[1] + " - " + blockedList[position].ToTime!!.split("T")[1]

    }

    fun setAnimationToTheRecyclerViewItem(holder: ViewHolder, position: Int) {
        holder.card.setOnClickListener( {
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

    fun unBlockRoom(mContext: Context,room: Unblock){
        var mUnBlockRoomViewModel= ViewModelProviders.of(mContext as BlockedDashboard).get(UnBlockRoomViewModel::class.java)

        mUnBlockRoomViewModel.unBlockRoom(mContext,room).observe(mContext, Observer {
            Toast.makeText(mContext, "UnBlock Room Successfully", Toast.LENGTH_SHORT).show()
            (mContext).mBlockedDashboardViewModel.mBlockDashboardRepository!!.makeApiCall(mContext)
        })
    }

    fun setFunctionOnButton(holder: ViewHolder,position: Int){
        holder.unblock.setOnClickListener {

            var builder = AlertDialog.Builder(mContext)
            builder.setTitle("Confirm ")
            builder.setMessage("Are you sure you want to unblock the Room?")
            builder.setPositiveButton("Yes"){dialog, which ->
                var block = Unblock()
                block.CId = blockedList[position].CId
                block.FromTime = blockedList[position].FromTime
                block.ToTime = blockedList[position].ToTime
                unBlockRoom(mContext, block)
            }
            builder.setNegativeButton("No"){dialog, which ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
            ColorOfDialogButton.setColorOfDialogButton(dialog)
        }
    }
}