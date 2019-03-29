package com.example.conferencerommapp.Helper

import com.google.gson.annotations.SerializedName

data class Unblock (
    @SerializedName("roomId")
    var cId: Int? = 0,
    @SerializedName("fromTime")
    var fromTime: String? = null,
    @SerializedName("toTime")
    var toTime: String? = null
)