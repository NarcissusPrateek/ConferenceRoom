package com.example.conferencerommapp

import com.google.gson.annotations.SerializedName


data class BuildingT(
    @SerializedName("buildingId")
    val BId: Int,
    @SerializedName("buildingName")
    val BName: String,
    @SerializedName("place")
    val Place : String
)