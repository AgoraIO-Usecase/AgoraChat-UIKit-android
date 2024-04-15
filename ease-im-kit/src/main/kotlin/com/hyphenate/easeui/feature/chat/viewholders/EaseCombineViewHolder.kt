package com.hyphenate.easeui.feature.chat.viewholders

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.activities.EaseChatHistoryActivity

class EaseCombineViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        skipToCombine(message)
    }

    private fun skipToCombine(message: ChatMessage?) {
        EaseChatHistoryActivity.actionStart(itemView.context, message)
    }

}