package com.example.myapplication.Models

import com.google.gson.annotations.SerializedName

data class ConferenceList(
        @SerializedName("roomName")
        val CName : String? = null,
        @SerializedName("capacity")
        val Capacity : Int? = 0
)