package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatLocationMessageBody

/**
 * location row
 */
open class ChatUIKitRowLocation @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : ChatUIKitRow(context, attrs, defStyleAttr, isSender) {
    private val locationView: TextView? by lazy { findViewById(R.id.tv_location) }
    private val tvLocationName: TextView? by lazy { findViewById(R.id.tv_location_name) }

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.uikit_row_received_location else R.layout.uikit_row_sent_location,
            this
        )
    }

    override fun onSetUpView() {
        (message?.body as? ChatLocationMessageBody)?.run {
            locationView?.text = address
        }
    }
}