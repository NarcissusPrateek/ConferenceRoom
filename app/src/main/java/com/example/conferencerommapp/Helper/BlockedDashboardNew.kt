package com.example.conferencerommapp.Helper

import android.annotation.SuppressLint
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
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Activity.BlockedDashboard
import com.example.conferencerommapp.Blocked
import com.example.conferencerommapp.R
import com.example.conferencerommapp.ViewModel.UnBlockRoomViewModel


class BlockedDashboardNew(private val blockedList: List<Blocked>, val mContext: Context, val listener: UnblockRoomListener) : androidx.recyclerview.widget.RecyclerView.Adapter<BlockedDashboardNew.ViewHolder>() {

    private var currentPosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.block_dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        setAnimationToTheRecyclerViewItem(holder, position)
        setDataToFields(holder, position)
        setFunctionOnButton(holder, position)


        holder.itemView.setOnClickListener {
            blockedList[position].roomId
        }
    }
    override fun getItemCount(): Int {
        return blockedList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this, itemView)
        }
        @BindView(R.id.block_conferenceRoomName)
        lateinit var conferenceName : TextView
        @BindView(R.id.block_building_name)
        lateinit var buildingName : TextView
        @BindView(R.id.block_purpose)
        lateinit var purpose : TextView
        @BindView(R.id.block_from_time)
        lateinit var fromtime : TextView
        @BindView(R.id.block_date)
        lateinit var date : TextView
        @BindView(R.id.card_block)
        lateinit var card: CardView
        @BindView(R.id.linearlayout_blocked)
        lateinit var linearLayout:LinearLayout
        val unblock :Button = itemView.findViewById(R.id.unblock)
        var blocked: Blocked? = null
    }

    @SuppressLint("SetTextI18n")
    fun setDataToFields(holder: ViewHolder, position: Int) {
        holder.blocked = blockedList[position]
        holder.conferenceName.text = blockedList[position].roomName
        holder.buildingName.text = blockedList[position].buildingName
        holder.purpose.text = blockedList[position].purpose
        holder.date.text = blockedList[position].fromTime!!.split("T")[0]
        holder.fromtime.text = blockedList[position].fromTime!!.split("T")[1] + " - " + blockedList[position].toTime!!.split("T")[1]

    }

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

    private fun unBlockRoom(mContext: Context, mRoom: Unblock){
        listener.onClickOfUnblock(mRoom)
    }

    private fun setFunctionOnButton(holder: ViewHolder, position: Int){
        holder.unblock.setOnClickListener {

            val builder = AlertDialog.Builder(mContext)
            builder.setTitle("Confirm ")
            builder.setMessage("Are you sure you want to unblock the Room?")
            builder.setPositiveButton("Yes"){_,_ ->
                val block = Unblock()
                block.cId = blockedList[position].roomId
                block.fromTime = blockedList[position].fromTime
                block.toTime = blockedList[position].toTime
                unBlockRoom(mContext, block)
            }
            builder.setNegativeButton("No"){_, _ ->
            }
            val dialog: AlertDialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
            ColorOfDialogButton.setColorOfDialogButton(dialog)
        }
    }

    interface UnblockRoomListener {
        fun onClickOfUnblock(mRoom: Unblock)
    }


}