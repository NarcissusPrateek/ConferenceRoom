package com.example.conferencerommapp.Helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.BuildingT
import com.example.conferencerommapp.R

class BuildingRecyclerAdapter(private val buildingList: List<BuildingT>, val btnlistener: BtnClickListener) : androidx.recyclerview.widget.RecyclerView.Adapter<BuildingRecyclerAdapter.ViewHolder>() {

    companion object {
        var mClickListener: BtnClickListener? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.building_dashboard_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mClickListener = btnlistener

        holder.building = buildingList[position]
        holder.txvBuilding.text = buildingList[position].buildingName
        val id = buildingList[position].buildingId
        val buildingname = buildingList[position].buildingName
        holder.itemView.setOnClickListener {
            mClickListener?.onBtnClick(id, buildingname)
        }
    }

    override fun getItemCount(): Int {
        return buildingList.size
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this, itemView)
        }
        @BindView(R.id.building_name)
        lateinit var txvBuilding: TextView
        var building: BuildingT? = null

        override fun toString(): String {
            return """${super.toString()} '${txvBuilding.text}'"""
        }
    }

    interface BtnClickListener {
        fun onBtnClick(buildingId: Int?,buildingname: String?)
    }
}

