package com.example.conferencerommapp.Helper


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R


class BuildingAdapter(var mContext: Context, private val mBuildingList: List<Building>, val btnlistener: BtnClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuildingAdapter.ViewHolder>() {

    /**
     * an interface object delacration
     */
    companion object {
        var mClickListener: BtnClickListener? = null
    }

    /**
     * attach view to the recyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    /**
     * return the number of item present in the list
     */
    override fun getItemCount(): Int {
        return mBuildingList.size
    }

    /**
     * bind data to the view
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener

        /**
         * set data into various fields of recylcerview card
         */
        holder.building = mBuildingList[position]
        holder.txvBuilding.text = mBuildingList[position].buildingName
        var id = mBuildingList[position].buildingId
        var building_name = mBuildingList[position].buildingName

        /**
         * call the interface method on click of item in recyclerview
         */
        holder.itemView.setOnClickListener { v ->
            mClickListener?.onBtnClick(id, building_name)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        var txvBuilding: TextView = itemView.findViewById(R.id.txv_building)
        var building: Building? = null
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    open interface BtnClickListener {
        fun onBtnClick(buildingId: String?, buildingname: String?)
    }
}