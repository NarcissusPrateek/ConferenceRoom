package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class BlockingConfirmation (
    @SerializedName("name")
    val Name: String? = null,
    @SerializedName("purpose")
    val Purpose: String? = null,
    var mStatus: Int? = 0
)