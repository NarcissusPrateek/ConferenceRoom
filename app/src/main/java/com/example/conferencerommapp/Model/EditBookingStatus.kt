package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class EditBookingStatus(
    @SerializedName("bookId")
    var bookingId: Int? = null,
    @SerializedName("flag")
    var flag: Boolean? = null
)