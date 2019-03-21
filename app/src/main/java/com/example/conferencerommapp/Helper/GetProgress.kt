package com.example.conferencerommapp.Helper

import android.app.ProgressDialog
import android.content.Context


class GetProgress {
    companion object {
        fun getProgressDialog(msg: String, context: Context) : ProgressDialog {
            var progressDialog = ProgressDialog(context)
            progressDialog.setMessage(msg)
            progressDialog.setCancelable(false)
            return progressDialog
        }
    }
}