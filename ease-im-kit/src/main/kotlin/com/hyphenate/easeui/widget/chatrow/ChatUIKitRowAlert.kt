package com.hyphenate.easeui.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatUIKitConstant

class ChatUIKitRowAlert(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : ChatUIKitRow(context, attrs, defStyleAttr, isSender) {
    private val contentView: TextView? by lazy { findViewById(R.id.text_content) }

    override fun onInflateView() {
        inflater.inflate(R.layout.uikit_row_unsent_message, this)
    }

    override fun onSetUpView() {
        message?.run {
            if (body is ChatCustomMessageBody) {
                val cBody = body as ChatCustomMessageBody
                if (cBody.event() == ChatUIKitConstant.MESSAGE_CUSTOM_ALERT) {
                    contentView?.text = cBody.params[ChatUIKitConstant.MESSAGE_CUSTOM_ALERT_CONTENT]
                }
            }
        }
    }
}