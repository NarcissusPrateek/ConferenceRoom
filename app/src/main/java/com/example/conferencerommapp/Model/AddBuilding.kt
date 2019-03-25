package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

//Model Class Of the AddBuilding
data class AddBuilding(
        @SerializedName("buildingName")
        var buildingName: String? = null,

        @SerializedName("place")
        var place:String? = null
)