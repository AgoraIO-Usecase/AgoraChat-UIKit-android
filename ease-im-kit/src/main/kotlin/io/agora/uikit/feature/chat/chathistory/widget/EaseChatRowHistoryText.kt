package io.agora.uikit.feature.chat.chathistory.widget

import android.content.Context
import android.util.AttributeSet
import io.agora.uikit.R
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.getDateFormat
import io.agora.uikit.widget.chatrow.EaseChatRowText

open class EaseChatRowHistoryText @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRowText(context, attrs, defStyleAttr, isSender) {

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_history_message, this)
    }

    override fun setOtherTimestamp(preMessage: ChatMessage?) {
        timeStampView?.let {
            preMessage?.let { msg ->
                it.text = msg.getDateFormat(true)
                it.visibility = VISIBLE
            }
        }
    }

    override fun onSetUpView() {
        super.onSetUpView()
        usernickView?.let {
            if (!it.text.toString().trim().isNullOrEmpty()) {
                it.visibility = VISIBLE
            }
        }
    }
}
