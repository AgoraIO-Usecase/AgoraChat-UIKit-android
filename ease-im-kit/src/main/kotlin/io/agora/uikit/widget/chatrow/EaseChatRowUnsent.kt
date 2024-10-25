package io.agora.uikit.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import io.agora.uikit.R
import io.agora.uikit.common.ChatTextMessageBody

class EaseChatRowUnsent(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRow(context, attrs, defStyleAttr, isSender) {
    private val contentView: TextView? by lazy { findViewById(R.id.text_content) }

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_unsent_message, this)
    }

    override fun onSetUpView() {
        message?.run {
            contentView?.text = (body as ChatTextMessageBody).message
        }
    }
}