package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class Dashboard (
    @SerializedName("fromTime")
    var fromTime : String? = null,

    @SerializedName("toTime")
    var toTime : String? = null,

    @SerializedName("buildingName")
    var buildingName : String? = null,

    @SerializedName("roomId")
    var roomId : Int? = null,

    @SerializedName("roomName")
    var roomName : String? = null,

    @SerializedName("purpose")
    var purpose: String? = null,

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("status")
    var status: String? = null,

    @SerializedName("name")
    var name: List<String>? = null,

    @SerializedName("cCMail")
    var cCMail: List<String>? = null
)

