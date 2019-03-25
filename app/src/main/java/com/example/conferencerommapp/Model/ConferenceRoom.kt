package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class ConferenceRoom(

    @SerializedName("roomId")
    var roomId: Int? = null,

    @SerializedName("roomName")
    var roomName: String? = null,

    @SerializedName("capacity")
    var roomCapacity: String? = null,

    @SerializedName("buildingId")
    var buildingId: String? = null,

    @SerializedName("status")
    var Status: String? = null

)