package com.example.conferencerommapp.Helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.R

class Conference_Room_adapter_new(private val conferenceRoomList: List<ConferenceRoom>, val btnlistener: BtnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<Conference_Room_adapter_new.ViewHolder>() {

    companion object {
        var mClickListener: BtnClickListener? = null
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_availablity, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        holder.conferenceRoom = conferenceRoomList[position]

        holder.txvRoom.text = conferenceRoomList[position].roomName

        holder.txvRoomCapacity.text = conferenceRoomList[position].roomCapacity

        if(conferenceRoomList[position].Status.equals("Available"))
        {
            holder.button!!.setBackgroundColor(Color.GREEN)
            holder.button!!.setOnClickListener {

            }
        }else if(conferenceRoomList[position].Status.equals("Booked")){
            holder.button!!.setBackgroundColor(Color.RED)
            holder.button!!.isEnabled = false
        }
        else {
            holder.button!!.setBackgroundColor(Color.YELLOW)
            holder.button!!.isEnabled = false
        }


        holder.itemView.setOnClickListener { v ->
            v.context
            val roomId = conferenceRoomList[position].roomId
            val roomname = conferenceRoomList[position].roomName
            mClickListener?.onBtnClick(roomId.toString(),roomname)
        }
    }

    override fun getItemCount(): Int {
        return conferenceRoomList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(itemView)
        }
        @BindView(R.id.txv_room)
        lateinit var txvRoom: TextView
        @BindView(R.id.txv_room_capacity)
        lateinit var txvRoomCapacity: TextView
        var conferenceRoom: ConferenceRoom? = null
        var button: Button? = null
        override fun toString(): String {
            return """${super.toString()} '${txvRoom.text}'"""
        }
    }

    interface BtnClickListener {
        fun onBtnClick(roomId: String?,roomname: String?)
    }

}