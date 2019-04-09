package com.example.conferencerommapp.ValidateField

class ValidateInputFields {
    companion object {
        fun validateInputForEmpty(input: String): Boolean {
            if (input.trim().isEmpty()) {
                return false
            }
            return true
        }
    }
}