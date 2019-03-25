package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class ManagerBooking(

    @SerializedName("email")
    var Email: String? = null,

    @SerializedName("roomId")
    var CId: Int? = 0,

    @SerializedName("buildingId")
    var BId: Int? = 0,

    @SerializedName("fromTime")
    var FromTime: ArrayList<String>? = null,

    @SerializedName("roomName")
    var CName: String? = null,

    @SerializedName("toTime")
    var ToTime: ArrayList<String>? = null,

    @SerializedName("purpose")
    var Purpose: String? = null,

    @SerializedName("cCMail")
    var CCMail: String? = null

)