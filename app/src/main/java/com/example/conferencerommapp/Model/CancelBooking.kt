package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class CancelBooking (

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = 0,

    @SerializedName("fromTime")
    var fromTime: String? = null,

    @SerializedName("toTime")
    var toTime: String? = null
)