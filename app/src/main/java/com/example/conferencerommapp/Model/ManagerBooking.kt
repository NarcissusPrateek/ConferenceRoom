package com.example.conferencerommapp.Model

data class ManagerBooking(
    var Email: String? = null,

    var CId: Int? = 0,

    var BId: Int? = 0,

    var FromTime: ArrayList<String>? = null,

    var CName: String? = null,

    var ToTime: ArrayList<String>? = null,

    var Purpose: String? = null,

    var CCMail: String? = null

)