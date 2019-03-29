package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName


data class BuildingT(
    @SerializedName("buildingId")
    val buildingId: Int,
    @SerializedName("buildingName")
    val buildingName: String,
    @SerializedName("place")
    val place : String
)