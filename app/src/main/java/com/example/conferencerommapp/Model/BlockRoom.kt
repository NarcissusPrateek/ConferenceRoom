package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class BlockRoom(
    @SerializedName("roomId")
    var CId: Int? = 0,
    @SerializedName("email")
    var Email: String? = null,
    @SerializedName("fromTime")
    var FromTime: String? = null,
    @SerializedName("toTime")
    var ToTime: String? = null,
    @SerializedName("buildingId")
    var BId: Int? = 0,
    @SerializedName("status")
    var Status: String? = null,
    @SerializedName("purpose")
    var Purpose: String? = null
)