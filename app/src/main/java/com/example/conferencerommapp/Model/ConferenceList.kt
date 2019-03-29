package com.example.myapplication.Models

import com.google.gson.annotations.SerializedName

data class ConferenceList(
        @SerializedName("roomName")
        val roomName : String? = null,
        @SerializedName("capacity")
        val capacity : Int? = 0
)