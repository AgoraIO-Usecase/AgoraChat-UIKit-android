package io.agora.uikit.widget.chatrow

import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import io.agora.uikit.R
import io.agora.uikit.common.ChatCustomMessageBody

class EaseChatRowCustom @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
) : EaseChatRow(context, attrs, defStyleAttr, isSender) {
    private val contentView: TextView? by lazy { findViewById(R.id.tv_chatcontent) }

    override fun onInflateView() {
        inflater.inflate(
            if (!isSender) R.layout.ease_row_received_message else R.layout.ease_row_sent_message,
            this
        )
    }

    override fun onSetUpView() {
        (message?.body as? ChatCustomMessageBody)?.let {
            contentView?.text = context.getString(R.string.ease_custom_message, it.event())
        }
    }

}