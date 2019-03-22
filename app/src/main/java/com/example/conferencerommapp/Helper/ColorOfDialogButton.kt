package com.example.conferencerommapp.Helper

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.widget.Button

class ColorOfDialogButton {
    companion object {
       fun setColorOfDialogButton(dialog: AlertDialog) {
           var mPositiveButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
           var mNegativeButton: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
           var mNeutralButton: Button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
           if (mPositiveButton != null) {
               mPositiveButton.setBackgroundColor(Color.WHITE)
               mPositiveButton.setTextColor(Color.parseColor("#0072bc"))
           }
           if(mNegativeButton != null) {
               mNegativeButton.setBackgroundColor(Color.WHITE)
               mNegativeButton.setTextColor(Color.parseColor("#0072bc"))
           }
           if(mNeutralButton != null) {
               mNeutralButton.setBackgroundColor(Color.WHITE)
               mNeutralButton.setTextColor(Color.parseColor("#FFFFFF"))
           }
       }
    }
}