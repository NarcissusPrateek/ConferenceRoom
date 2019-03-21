package com.example.conferencerommapp.Model

import java.io.Serializable

class GetIntentDataFromActvity(
    var fromtime: String? = null,
    var totime: String? = null,
    var date: String? = null,
    var capacity: String? = null,
    var buildingName: String? = null,
    var roomName: String? = null,
    var roomId: String? = null,
    var buildingId: String? = null
) : Serializable