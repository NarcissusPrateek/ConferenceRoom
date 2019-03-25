package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class ManagerConference(

    @SerializedName("fromTime")
    var FromTime : ArrayList<String>? = null,

    @SerializedName("toTime")
    var ToTime : ArrayList<String>? = null,

    @SerializedName("buildingId")
    var BId : Int? = 0
)