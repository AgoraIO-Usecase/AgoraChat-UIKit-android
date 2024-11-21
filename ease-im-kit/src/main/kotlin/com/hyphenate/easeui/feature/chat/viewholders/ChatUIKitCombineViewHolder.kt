package com.hyphenate.easeui.feature.chat.viewholders

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.activities.ChatUIKitHistoryActivity

class ChatUIKitCombineViewHolder(itemView: View) : ChatUIKitRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        skipToCombine(message)
    }

    private fun skipToCombine(message: ChatMessage?) {
        ChatUIKitHistoryActivity.actionStart(itemView.context, message)
    }

}