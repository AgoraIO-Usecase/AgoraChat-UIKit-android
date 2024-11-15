package com.hyphenate.easeui.feature.chat.viewholders

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.helper.ChatUIKitDingMessageHelper

class ChatUIKitUnknownViewHolder(itemView: View) : ChatUIKitRowViewHolder(itemView) {
    override fun handleReceiveMessage(message: ChatMessage?) {
        super.handleReceiveMessage(message)
        message?.let {
            // Send the group-ack cmd type msg if this msg is a ding-type msg.
            ChatUIKitDingMessageHelper.get().sendAckMessage(it)
        }
    }
}