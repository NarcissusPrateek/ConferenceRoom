package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

data class BuildingConference(

    @SerializedName("roomId")
    val CId: Int = 0,
    @SerializedName("roomName")
    val CName: String? = null
)