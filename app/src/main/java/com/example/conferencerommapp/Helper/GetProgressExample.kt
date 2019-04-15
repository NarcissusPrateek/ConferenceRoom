@file:Suppress("DEPRECATION")

package com.example.conferencerommapp.Helper

import android.app.ProgressDialog
import android.content.Context


class GetProgressExample {
        fun getProgressDialog(msg: String, context: Context) : ProgressDialog {
            val progressDialog = ProgressDialog(context)
            progressDialog.setMessage(msg)
            progressDialog.setCancelable(false)
            return progressDialog
        }
}