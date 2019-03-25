package com.example.conferencerommapp.Helper

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.widget.Button

class ColorOfDialogButton {

    /**
     * this object provides a static method for setting the color property for the alert dialog button
     */
    companion object {
        fun setColorOfDialogButton(dialog: AlertDialog) {
            var mPositiveButton: Button = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
            var mNegativeButton: Button = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
            var mNeutralButton: Button = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)

            /**
             * for positive button color code is #0072bc
             */
            if (mPositiveButton != null) {
                mPositiveButton.setBackgroundColor(Color.WHITE)
                mPositiveButton.setTextColor(Color.parseColor("#0072bc"))
            }

            /**
             * for Negative button color code Black
             */
            if (mNegativeButton != null) {
                mNegativeButton.setBackgroundColor(Color.WHITE)
                mNegativeButton.setTextColor(Color.BLACK)
            }

            /**
             * for Neutral button color is RED
             */
            if (mNeutralButton != null) {
                mNeutralButton.setBackgroundColor(Color.WHITE)
                mNeutralButton.setTextColor(Color.RED)
            }
        }
    }
}