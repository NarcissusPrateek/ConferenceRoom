package com.example.conferencerommapp.Model

data class BookingDetails(
    var fromTime: String? = null,
    var toTime: String? = null,
    var date: String? = null,
    var roomName: String? = null,
    var buildingName: String? = null,
    var capacity: String? = null,
    var cId: String? = null,
    var bId: String? = null
)