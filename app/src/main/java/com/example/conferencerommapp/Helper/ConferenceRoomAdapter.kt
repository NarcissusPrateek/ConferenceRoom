package com.example.conferencerommapp.Helper

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.cardview.widget.CardView
import butterknife.BindView
import butterknife.ButterKnife
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

        holder.itemView.setOnClickListener {
            if (holder.txvStatus.text == "Available") {
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
        init {
            ButterKnife.bind(this,itemView)
        }
        @BindView(R.id.txv_room)
        lateinit var txvRoom: TextView
        @BindView(R.id.txv_room_capacity)
        lateinit var txvRoomCapacity: TextView
        @Nullable
        @BindView(R.id.status_txv)
        lateinit var txvStatus: TextView
        @BindView(R.id.cardview2)
        lateinit var cardview: CardView
        var conferenceRoom: ConferenceRoom? = null
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface BtnClickListener {
        fun onBtnClick(roomId: String?, roomname: String?)
    }

    /**
     * set data to the different view items
     */
    private fun setDataToViewItems(holder: ViewHolder, position: Int) {
        holder.conferenceRoom = mConferenceRoomList[position]
        holder.txvRoom.text = mConferenceRoomList[position].roomName
        holder.txvRoomCapacity.text = mConferenceRoomList[position].roomCapacity
        holder.txvStatus.text = mConferenceRoomList[position].status
    }

    /**
     * function will change the color of status field based on status
     */
    private fun changeColorOfStatusField(holder: ViewHolder) {
        when {
            holder.txvStatus.text.equals("Available") -> holder.txvStatus.setTextColor(Color.parseColor("#4C9A2A"))
            holder.txvStatus.text.equals("Booked") -> holder.txvStatus.setTextColor(Color.parseColor("#A9A9A9"))
            holder.txvStatus.text.equals("Blocked") -> holder.txvStatus.setTextColor(Color.parseColor("#FE7D6A"))
        }
    }
}
