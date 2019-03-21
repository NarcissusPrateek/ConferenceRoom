package com.example.conferencerommapp.Helper


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R

class BuildingAdapter(context: Context, private val mBuildingList: List<Building>, val btnlistener: BtnClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuildingAdapter.ViewHolder>() {
    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mBuildingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        holder.building = mBuildingList[position]
        holder.txvBuilding.text = mBuildingList[position].building_name
        var id = mBuildingList[position].building_id
        var building_name = mBuildingList[position].building_name
        holder.itemView.setOnClickListener { v ->
            mClickListener?.onBtnClick(id, building_name)
        }
    }

    class ViewHolder(iteView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(iteView) {
        val txvBuilding: TextView = itemView.findViewById(R.id.txv_building)
        var building: Building? = null
        override fun toString(): String {
            return """${super.toString()} '${txvBuilding.text}'"""
        }
    }

    open interface BtnClickListener {
        fun onBtnClick(buildingId: String?, buildingname: String?)
    }
}


/*class BuildingAdapter(private val mEmployeeList: List<Building>, val btnlistener: BtnClickListener) :
    androidx.recyclerview.widget.RecyclerView.Adapter<BuildingAdapter.ViewHolder>() {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener
        holder.building = mEmployeeList[position]
        holder.txvBuilding.text = mEmployeeList[position].building_name
        var id = mEmployeeList[position].building_id
        var building_name = mEmployeeList[position].building_name
        holder.itemView.setOnClickListener { v ->
            mClickListener?.onBtnClick(id, building_name)
        }
    }

    override fun getItemCount(): Int {
        return mEmployeeList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val txvBuilding: TextView = itemView.findViewById(R.id.txv_building)
        var building: Building? = null
        override fun toString(): String {
            return """${super.toString()} '${txvBuilding.text}'"""
        }
    }

    open interface BtnClickListener {
        fun onBtnClick(buildingId: String?, buildingname: String?)
    }
}
*/


