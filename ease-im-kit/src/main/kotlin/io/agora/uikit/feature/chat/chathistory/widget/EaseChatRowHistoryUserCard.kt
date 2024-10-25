package io.agora.uikit.feature.chat.chathistory.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import io.agora.uikit.R
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.getDateFormat
import io.agora.uikit.common.extensions.getUserCardInfo
import io.agora.uikit.common.extensions.isUserCardMessage
import io.agora.uikit.widget.chatrow.EaseChatRowUserCard

open class EaseChatRowHistoryUserCard @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRowUserCard(context, attrs, defStyleAttr, isSender) {

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_history_message, this)
    }

    override fun onSetUpView() {
        message?.run {
            if (isUserCardMessage()) {
                val contentView = findViewById<TextView>(R.id.tv_chatcontent)
                contentView.text = context.getString(R.string.ease_user_card, getUserCardInfo()?.name ?: "")
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
