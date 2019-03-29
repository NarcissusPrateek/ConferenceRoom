package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

//Model Class Of the AddConference
data class AddConferenceRoom(
   @SerializedName("buildingId")
   var  bId:Int?= 0,

   @SerializedName("roomName")
   var roomName :String?=null,

   @SerializedName("capacity")
   var capacity :Int? = 0

)