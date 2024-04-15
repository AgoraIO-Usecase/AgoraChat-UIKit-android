package com.hyphenate.easeui.demo.base

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StringRes
import com.hyphenate.easeui.demo.R

class EaseProgressDialog protected constructor(context: Context, themeResId: Int = 0) :
    ProgressDialog(context, themeResId) {
    class Builder(private val mContext: Context) {
        private var message: String? = null
        private var cancelable = false
        private var canceledOnTouchOutside = false
        private var cancelListener: DialogInterface.OnCancelListener? = null
        fun setLoadingMessage(@StringRes message: Int): Builder {
            this.message = mContext.getString(message)
            return this
        }

        fun setLoadingMessage(message: String?): Builder {
            this.message = message
            return this
        }

        fun setCancelable(cancelable: Boolean): Builder {
            this.cancelable = cancelable
            return this
        }

        fun setCanceledOnTouchOutside(cancel: Boolean): Builder {
            canceledOnTouchOutside = cancel
            return this
        }

        fun setOnCancelListener(cancelListener: DialogInterface.OnCancelListener?): Builder {
            this.cancelListener = cancelListener
            return this
        }

        fun build(): EaseProgressDialog {
            val dialog = EaseProgressDialog(mContext, R.style.Dialog_Light)
            dialog.setCancelable(cancelable)
            dialog.setCanceledOnTouchOutside(canceledOnTouchOutside)
            dialog.setMessage(message)
            dialog.setOnCancelListener(cancelListener)
            return dialog
        }

        fun show(): EaseProgressDialog {
            val dialog = build()
            dialog.show()
            return dialog
        }
    }
}