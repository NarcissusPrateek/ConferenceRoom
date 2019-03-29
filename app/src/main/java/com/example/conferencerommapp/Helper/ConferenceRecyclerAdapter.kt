package com.example.conferencerommapp.Helper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.conferencerommapp.R

import com.example.myapplication.Models.ConferenceList

class ConferenceRecyclerAdapter(private val conferencceList:List<ConferenceList>) : androidx.recyclerview.widget.RecyclerView.Adapter<ConferenceRecyclerAdapter.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.conference_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  conferencceList.size

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.conferencelist=conferencceList[position]
        holder.conferenceName.text=conferencceList[position].CName
        holder.conferencecapacity.text= conferencceList[position].Capacity.toString()
    }

    class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
            val conferenceName : TextView = itemView.findViewById(R.id.room_name_show)
            val conferencecapacity : TextView = itemView.findViewById(R.id.conference_room_capacity_show)
            var conferencelist: ConferenceList?= null
    }

}