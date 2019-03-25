package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class EmployeeList (

    @SerializedName("email")
    var email: String? = null,

    @SerializedName("name")
    var name: String? = null,

    var isSelected: Boolean? = null
)