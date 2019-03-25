package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class Dashboard (

    @SerializedName("fromTime")
    var FromTime : String? = null,

    @SerializedName("toTime")
    var ToTime : String? = null,

    @SerializedName("buildingName")
    var BName : String? = null,

    @SerializedName("roomId")
    var CId : Int? = null,

    @SerializedName("roomName")
    var CName : String? = null,

    @SerializedName("purpose")
    var Purpose: String? = null,

    @SerializedName("email")
    var Email: String? = null,

    @SerializedName("status")
    var Status: String? = null,

    @SerializedName("name")
    var Name: List<String>? = null
)

