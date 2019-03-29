package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName

data class BuildingConference(

    @SerializedName("roomId")
    val roomId: Int = 0,
    @SerializedName("roomName")
    val roomName: String? = null
)