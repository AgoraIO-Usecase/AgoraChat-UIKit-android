package com.hyphenate.easeui.feature.chat.viewholders

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.helper.EaseDingMessageHelper

class EaseCustomViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun handleReceiveMessage(message: ChatMessage?) {
        super.handleReceiveMessage(message)
        message?.let {
            // Send the group-ack cmd type msg if this msg is a ding-type msg.
            EaseDingMessageHelper.get().sendAckMessage(it)
        }
    }
}