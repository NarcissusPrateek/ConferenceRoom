package com.example.conferencerommapp.Model

class Manager {
    var FromTime: String? = null
    var ToTime: String? = null
    var BName: String? = null
    var CId: Int? = null
    var CName: String? = null
    var Purpose: String? = null
    var Email: String? = null
    var Status =  ArrayList<String>()
    var fromlist = ArrayList<String>()
    var Name: List<String>? = null
    var cCMail: List<String>? = null
    override fun toString(): String {
        return "Manager(FromTime=$FromTime, ToTime=$ToTime, BName=$BName, CId=$CId, CName=$CName, Purpose=$Purpose, Email=$Email, Status=$Status, fromlist=$fromlist, Name=$Name, cCMail=$cCMail)"
    }
}

