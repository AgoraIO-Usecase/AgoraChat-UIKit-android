package com.hyphenate.easeui.feature.chat.chathistory.widget

import android.content.Context
import android.util.AttributeSet
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatNormalFileMessageBody
import com.hyphenate.easeui.common.extensions.getDateFormat
import com.hyphenate.easeui.widget.chatrow.ChatUIKitRowFile

/**
 * file for row
 */
open class ChatUIKitRowHistoryFile @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : ChatUIKitRowFile(context, attrs, defStyleAttr, isSender) {

    override fun onInflateView() {
        inflater.inflate(R.layout.uikit_row_history_file, this)
    }

    override fun onSetUpView() {
        message?.run {
            (body as? ChatNormalFileMessageBody)?.let {
                fileNameView?.text = "${context.getString(R.string.uikit_file)} ${it.fileName}"
            }
        }
        usernickView?.let {
            if (!it.text.toString().trim().isNullOrEmpty()) {
                it.visibility = VISIBLE
            }
        }
    }

    override fun setOtherTimestamp(preMessage: ChatMessage?) {
        timeStampView?.let {
            preMessage?.let { msg ->
                it.text = msg.getDateFormat(true)
                it.visibility = VISIBLE
            }
        }
    }
}
