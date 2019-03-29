package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class Employee (


    @SerializedName("email")
    var email: String? = null,

    @SerializedName("employeeId")
    var employeeId: String? = null,

    @SerializedName("name")
    var name: String? = null,

    @SerializedName("role")
    var role: String? = null,

    @SerializedName("activationCode")
    var activationCode: String? = null,

    @SerializedName("verified")
    var verified: Boolean? = false

)