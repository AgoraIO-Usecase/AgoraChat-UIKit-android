package io.agora.uikit.feature.chat.chathistory.widget

import android.content.Context
import android.util.AttributeSet
import io.agora.uikit.R
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.getDateFormat
import io.agora.uikit.widget.chatrow.EaseChatRowCombine

open class EaseChatRowHistoryCombine @JvmOverloads constructor(
    private val context: Context,
    private val attrs: AttributeSet? = null,
    private val defStyleAttr: Int = 0,
    isSender: Boolean = false
): EaseChatRowCombine(context, attrs, defStyleAttr, isSender) {

    override fun onInflateView() {
        inflater.inflate(R.layout.ease_row_history_combine, this)
    }

    override fun onSetUpView() {
        contentView?.text = context.getString(R.string.ease_combine)
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
