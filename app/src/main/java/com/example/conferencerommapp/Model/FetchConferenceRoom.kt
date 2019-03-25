package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName
import java.time.chrono.ChronoLocalDateTime

data class FetchConferenceRoom (

    @SerializedName("fromTime")
    var fromTime : String? = null,

    @SerializedName("toTime")
    var toTime : String? = null,

    @SerializedName("buildingId")
    var buildingId : Int? = 0,

    @SerializedName("capacity")
    var capacity : Int? = 0

)