package io.agora.uikit.feature.chat.chathistory.widget

import android.content.Context
import android.util.AttributeSet
import io.agora.uikit.R
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatNormalFileMessageBody
import io.agora.uikit.common.extensions.getDateFormat
import io.agora.uikit.widget.chatrow.EaseChatRowFile

/**
 * file for row
 */
open class EaseChatRowHistoryFile @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRowFile(context, attrs, defStyleAttr, isSender) {

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_history_file, this)
    }

    override fun onSetUpView() {
        message?.run {
            (body as? ChatNormalFileMessageBody)?.let {
                fileNameView?.text = "${context.getString(R.string.ease_file)} ${it.fileName}"
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
