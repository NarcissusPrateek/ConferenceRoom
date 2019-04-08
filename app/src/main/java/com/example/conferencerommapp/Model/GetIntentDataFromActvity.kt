package com.example.conferencerommapp.Model

import java.io.Serializable

class GetIntentDataFromActvity(
    var fromTime: String? = null,
    var toTime: String? = null,
    var date: String? = null,
    var capacity: String? = null,
    var buildingName: String? = null,
    var roomName: String? = null,
    var roomId: String? = null,
    var buildingId: String? = null,
    var listOfDays: ArrayList<Int> = ArrayList(),
    var toDate: String? = null,
    var fromTimeList: ArrayList<String> = ArrayList(),
    var toTimeList: ArrayList<String> = ArrayList(),
    var purpose: String? = null,
    var cCMail: List<String>? = null
) : Serializable