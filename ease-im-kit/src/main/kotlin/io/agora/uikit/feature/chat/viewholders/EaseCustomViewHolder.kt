package io.agora.uikit.feature.chat.viewholders

import android.view.View
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.helper.EaseDingMessageHelper

class EaseCustomViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun handleReceiveMessage(message: ChatMessage?) {
        super.handleReceiveMessage(message)
        message?.let {
            // Send the group-ack cmd type msg if this msg is a ding-type msg.
            EaseDingMessageHelper.get().sendAckMessage(it)
        }
    }
}