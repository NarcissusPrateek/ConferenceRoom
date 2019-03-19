package com.example.conferencerommapp.Helper

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.conferencerommapp.Model.EmployeeList
import com.example.conferencerommapp.R


class CheckBoxAdapter(var employee: ArrayList<EmployeeList>,var checkedEmployee: ArrayList<EmployeeList>, var context: Context) : RecyclerView.Adapter<CheckBoxAdapter.ViewHolder>() {


    //var checkedTeachers = ArrayList<EmployeeList>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_alertdialog, null)

        return ViewHolder(v)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val teacher = employee[position]
        holder.nameTxt.text = teacher.Name
        holder.myCheckBox.isChecked = teacher.isSelected!!
        if(teacher.isSelected!!) {
            if(checkedEmployee.contains(teacher)) {

            }else {
                checkedEmployee.add(teacher)
            }
        }
        holder.setItemClickListener(object : ViewHolder.ItemClickListener {
            override fun onItemClick(v: View, pos: Int) {
                val myCheckBox = v as CheckBox
                val currentTeacher = employee[pos]
                if (myCheckBox.isChecked) {
                    currentTeacher.isSelected = true
                    checkedEmployee.add(currentTeacher)
                } else if (!myCheckBox.isChecked) {
                    currentTeacher.isSelected = false
                    checkedEmployee.remove(currentTeacher)
                }
            }
        })
    }
    override fun getItemCount(): Int {
        return employee.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var nameTxt: TextView
        //var emailTxt: TextView
        var myCheckBox: CheckBox

        lateinit var myItemClickListener: ItemClickListener

        init {
            nameTxt = itemView.findViewById(R.id.textViewName)
            //emailTxt = itemView.findViewById(R.id.textViewEmail)
            myCheckBox = itemView.findViewById(R.id.checkBox)
            myCheckBox.setOnClickListener(this)
        }

        fun setItemClickListener(ic: ItemClickListener) {
            this.myItemClickListener = ic
        }

        override fun onClick(v: View) {
            this.myItemClickListener.onItemClick(v, layoutPosition)
        }

        interface ItemClickListener {
            fun onItemClick(v: View, pos: Int)
        }
    }

    fun filterList(filterdNames: ArrayList<EmployeeList>) {
        this.employee = filterdNames
        notifyDataSetChanged()
    }

    fun getList(): ArrayList<EmployeeList> {
        return checkedEmployee
    }
}
