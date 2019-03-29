package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

data class Blocked(

    @SerializedName("roomId")
    val roomId : Int? =0,

    @SerializedName("buildingName")
    val buildingName : String? = null,

    @SerializedName("roomName")
    val roomName : String? = null,

    @SerializedName("fromTime")
    val fromTime: String? = null,

    @SerializedName("toTime")
    val toTime: String? = null,

    @SerializedName("purpose")
    val purpose: String? = null

)