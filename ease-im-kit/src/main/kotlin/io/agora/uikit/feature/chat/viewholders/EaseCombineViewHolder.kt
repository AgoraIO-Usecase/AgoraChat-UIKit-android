package io.agora.uikit.feature.chat.viewholders

import android.view.View
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.feature.chat.activities.EaseChatHistoryActivity

class EaseCombineViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        skipToCombine(message)
    }

    private fun skipToCombine(message: ChatMessage?) {
        EaseChatHistoryActivity.actionStart(itemView.context, message)
    }

}