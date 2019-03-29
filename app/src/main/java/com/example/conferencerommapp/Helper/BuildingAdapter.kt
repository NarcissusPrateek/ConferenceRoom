package com.example.conferencerommapp.Helper


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.conferencerommapp.Model.Building
import com.example.conferencerommapp.R


class BuildingAdapter(var mContext: Context, private val mBuildingList: List<Building>, private val btnListener: BtnClickListener) :
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
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
        mClickListener = btnListener

        /**
         * set data into various fields of recylcerview card
         */
        holder.building = mBuildingList[position]
        holder.txvBuilding.text = mBuildingList[position].buildingName
        val id = mBuildingList[position].buildingId
        val buildingName = mBuildingList[position].buildingName

        /**
         * call the interface method on click of item in recyclerview
         */
        holder.itemView.setOnClickListener {
            mClickListener?.onBtnClick(id, buildingName)
        }
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        init {
            ButterKnife.bind(this, itemView)
        }
        @BindView(R.id.txv_building)
        lateinit var txvBuilding: TextView
        var building: Building? = null
    }

    /**
     * an Interface which needs to be implemented whenever the adapter is attached to the recyclerview
     */
    interface BtnClickListener {
        fun onBtnClick(buildingId: String?, buildingname: String?)
    }
}