package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

//Model Class Of the AddConference
data class AddConferenceRoom(
   @SerializedName("buildingId")
   var  BId:Int?= 0,

   @SerializedName("roomName")
   var CName :String?=null,

   @SerializedName("capacity")
   var Capacity :Int? = 0

)