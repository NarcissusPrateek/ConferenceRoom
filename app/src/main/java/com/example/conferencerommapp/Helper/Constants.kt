package com.example.conferencerommapp.Helper

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
        const val IP_ADDRESS = "http://192.168.1.192/CRB/"
    }
}