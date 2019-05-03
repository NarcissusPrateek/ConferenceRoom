package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class UpdateBooking(
    @SerializedName("email")
    var email: String? = null,

    @SerializedName("roomId")
    var roomId: Int? = 0,

    @SerializedName("fromTime")
    var fromTime: String? = null,

    @SerializedName("newFromTime")
    var newfromTime: String? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("toTime")
    var toTime: String? = null,

    @SerializedName("newToTime")
    var newtotime: String? =null,

    @SerializedName("cCMail")
    var cCMail: String? = null,

    @SerializedName("bookId")
    var bookingId: Int? = null
)