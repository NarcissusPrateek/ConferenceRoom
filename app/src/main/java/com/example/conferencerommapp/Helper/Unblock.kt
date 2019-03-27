package com.example.conferencerommapp.Helper

import com.google.gson.annotations.SerializedName

data class Unblock (
    @SerializedName("roomId")
    var CId: Int? = 0,
    @SerializedName("fromTime")
    var FromTime: String? = null,
    @SerializedName("toTime")
    var ToTime: String? = null
)