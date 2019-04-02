package com.example.conferencerommapp.Model

import com.google.gson.annotations.SerializedName

data class Employee (


    @SerializedName("Email")
    var email: String? = null,

    @SerializedName("EmpId")
    var employeeId: String? = null,

    @SerializedName("Name")
    var name: String? = null,

    @SerializedName("Role")
    var role: String? = null,

    @SerializedName("ActivationCode")
    var activationCode: String? = null,

    @SerializedName("Verified")
    var verified: Boolean? = false

)