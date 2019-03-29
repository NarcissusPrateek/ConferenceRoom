package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class ManagerConference(

    @SerializedName("fromTime")
    var fromTime : ArrayList<String>? = null,

    @SerializedName("toTime")
    var toTime : ArrayList<String>? = null,

    @SerializedName("buildingId")
    var buildingId : Int? = 0
)