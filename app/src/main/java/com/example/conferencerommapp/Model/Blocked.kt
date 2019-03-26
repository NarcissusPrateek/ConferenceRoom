package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

data class Blocked(

    @SerializedName("roomId")
    val CId : Int? =0,

    @SerializedName("buildingName")
    val BName : String? = null,

    @SerializedName("roomName")
    val CName : String? = null,

    @SerializedName("fromTime")
    val FromTime: String? = null,

    @SerializedName("toTime")
    val ToTime: String? = null,

    @SerializedName("purpose")
    val Purpose: String? = null

)