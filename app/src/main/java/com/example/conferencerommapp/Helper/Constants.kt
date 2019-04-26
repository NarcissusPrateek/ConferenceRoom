package com.example.conferencerommapp.Helper

import android.content.SharedPreferences

class Constants {
    /**
     * it will provides some static final constants
     */
    companion object {


        /**
         * to check the status of user whether registered or not
         */
        const val EXTRA_REGISTERED = "com.example.conferencerommapp.Activity.EXTRA_REGISTERED"

        /**
         * for set and get intent data
         */
        const val EXTRA_INTENT_DATA = "com.example.conferencerommapp.Activity.EXTRA_INTENT_DATA"

        /**
         * response code for response IsSuccessfull
         */
        const val OK_RESPONSE = 200

        /**
         * building id Name for intents
         */
        const val EXTRA_BUILDING_ID = "com.example.conferencerommapp.Activity.EXTRA_BUILDING_ID"

        /**
         * ip address for api call
         */
        var IP_ADDRESS = "http://192.168.1.197/CRB/"

        const val SOME_EXCEPTION = 400

        const val HR_CODE = 11

        const val MANAGER_CODE = 12

        const val EMPLOYEE_CODE = 10
    }
}