package com.example.conferencerommapp.Helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.conferencerommapp.Model.ConferenceRoom
import com.example.conferencerommapp.R


class ConferenceRoomAdapter(private val mConferenceRoomList: List<ConferenceRoom>, val btnlistener: BtnClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<ConferenceRoomAdapter.ViewHolder>() {

    /**
     * an interface object delacration
     */
    companion object {
        var mClickListener: BtnClickListener? = null
    }

    /**
     * attach view to the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.room_availablity, parent, false)
        return ViewHolder(view)
    }


    /**
     * bind data to the view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        setDataToViewItems(holder, position)

        changeColorOfStatusField(holder)

        holder.itemView.setOnClickListener { v ->
            if (holder.txvStatus.text.equals("Available")) {
                val roomId = mConferenceRoomList[position].roomId
                val roomName = mConferenceRoomList[position].roomName

                /**
                 * call the interface method
                 */
                mClickListener?.onBtnClick(roomId.toString(), roomName)
            }
        }
    }

    /**
     * return the number of item present in the list
     */
    override fun getItemCount(): Int {
        return mConferenceRoomList.size
    }


    /**
     * get all fields from view
     */
    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        val txvRoom: TextView = itemView.findViewById(R.id.txv_room)
        val txvRoomCapacity: TextView = itemView.findViewById(R.id.txv_room_capacity)
        val txvStatus: TextView = itemView.findViewById(R.id.status_txv)
        var cardview: CardView = itemView.findViewById(R.id.cardview2)
        var conferenceRoom: ConferenceRoom? = null
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    open interface BtnClickListener {
        fun onBtnClick(roomId: String?, roomname: String?)
    }

    /**
     * set data to the different view items
     */
    fun setDataToViewItems(holder: ViewHolder, position: Int) {
        holder.conferenceRoom = mConferenceRoomList[position]
        holder.txvRoom.text = mConferenceRoomList[position].roomName
        holder.txvRoomCapacity.text = mConferenceRoomList[position].roomCapacity
        holder.txvStatus.text = mConferenceRoomList[position].Status
    }

    /**
     * function will change the color of status field based on status
     */
    fun changeColorOfStatusField(holder: ViewHolder) {
        if (holder.txvStatus.text.equals("Available")) {
            holder.txvStatus.setTextColor(Color.parseColor("#4C9A2A"))
        } else if (holder.txvStatus.text.equals("Booked")) {
            holder.txvStatus.setTextColor(Color.parseColor("#A9A9A9"))
        } else if (holder.txvStatus.text.equals("Blocked")) {
            holder.txvStatus.setTextColor(Color.parseColor("#FE7D6A"))
        }
    }
}
