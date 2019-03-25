package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class Booking (

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = 0,

    @SerializedName("buildingId")
    var buildingId: Int? = 0,

    @SerializedName("fromTime")
    var fromTime: String? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("toTime")
    var toTime: String? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("cCMail")
    var cCMail: String? = null
)
