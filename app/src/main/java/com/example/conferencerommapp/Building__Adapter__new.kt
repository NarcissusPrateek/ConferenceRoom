package com.nineleaps.buttonchecker

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.example.conferencerommapp.R
import java.text.SimpleDateFormat
import java.util.*


class Building__Adapter__new(private val buildingList: List<Int>,val context:Context) : androidx.recyclerview.widget.RecyclerView.Adapter<Building__Adapter__new.ViewHolder>() {



	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

		val view = LayoutInflater.from(parent.context).inflate(R.layout.prateek_list__item_new, parent, false)
		return ViewHolder(view)
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		holder.txvBuilding.text = buildingList[position].toString()
//		val startTime = "23/02/2019 10:00"
//		try {
//
//			val sdf = SimpleDateFormat("dd/M/yyyy hh:mm")
//			val cal: Calendar = Calendar.getInstance()
//			val d1 = sdf.parse("24/02/2019  " + buildingList[position])
//			val elapsed = d1.getTime() - cal.timeInMillis
//			Log.i("-------------", elapsed.toString())
//			if(elapsed > 0) {
//				holder.button.text = "Cancle"
//				holder.button.setBackgroundColor(Color.RED)
//			}
//			else {
//				holder.button.text = "Delete"
//				holder.button.setBackgroundColor(Color.GREEN)
//			}
//		} catch (e: Exception) {
//			Log.i("--------------", e.toString())
//		}
//		holder.button.setOnClickListener {
//			if(holder.button.text.equals("Cancle")) {
//				var builder = AlertDialog.Builder(context)
//				builder.setTitle("Confirm ")
//				builder.setMessage("Press ok to Cancel the meeting")
//				builder.setPositiveButton("OK"){dialog, which ->
//
//				}
//				builder.setNegativeButton("cancle"){ dialog, which ->
//					Toast.makeText(context,"Cancled",Toast.LENGTH_SHORT).show()
//				}
//				val dialog: AlertDialog = builder.create()
//				dialog.show()
//			}
//			else {
//				Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show()
//			}
//		}
		if(buildingList[position]%2 == 0) {
			holder.button.text = "EVEN"
			holder.cardview.setCardBackgroundColor(Color.RED)
		}
		else {
			holder.button.text = "ODD"
		}
		holder.itemView.setOnClickListener{
			if(holder.button.text.equals("ODD")){

			}
			else {
				Toast.makeText(context,"EvEN",Toast.LENGTH_SHORT).show()
			}
		}




	}
    override fun getItemCount(): Int {
		return buildingList.size
	}

	class ViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val txvBuilding: TextView = itemView.findViewById(R.id.txv_building)
		val button: Button = itemView.findViewById(R.id.button3)
		val cardview: CardView = itemView.findViewById(R.id.cardview)

		override fun toString(): String {
			return """${super.toString()} '${txvBuilding.text}'"""
		}
	}

}
