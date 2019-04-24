package com.example.conferencerommapp.Helper

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.example.conferencerommapp.SignIn
import com.google.android.gms.common.internal.ConnectionErrorMessages

class GetAleretDialog {
    companion object {
        fun getDialog(mContext: Context, title: String, message: String): AlertDialog.Builder {
            val mDialog = AlertDialog.Builder(mContext)
            mDialog.setTitle(title)
            mDialog.setMessage(message)
            return mDialog
        }

        fun showDialog(mDialog: AlertDialog.Builder): AlertDialog {
            val dialog: AlertDialog = mDialog.create()
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
            return dialog
        }
    }
}