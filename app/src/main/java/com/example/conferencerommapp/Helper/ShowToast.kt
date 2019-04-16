package com.example.conferencerommapp.Helper

import android.content.Context
import android.widget.Toast
import com.example.conferencerommapp.R
import es.dmoral.toasty.Toasty

/**
 * show different toast for different kind of error and response messages
 */
class ShowToast {
    companion object {
        fun show(mContext: Context, message: String) {
            if(message == mContext.getString(R.string.internal_server)) {
                Toasty.error(mContext, message, Toast.LENGTH_SHORT, true).show()
            }else {
                Toasty.info(mContext, message, Toast.LENGTH_SHORT, true).show()
            }
        }
    }
}