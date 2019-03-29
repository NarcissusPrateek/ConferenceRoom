package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class BlockRoom(
    @SerializedName("roomId")
    var cId: Int? = 0,
    @SerializedName("email")
    var email: String? = null,
    @SerializedName("fromTime")
    var fromTime: String? = null,
    @SerializedName("toTime")
    var toTime: String? = null,
    @SerializedName("buildingId")
    var bId: Int? = 0,
    @SerializedName("status")
    var status: String? = null,
    @SerializedName("purpose")
    var purpose: String? = null
)