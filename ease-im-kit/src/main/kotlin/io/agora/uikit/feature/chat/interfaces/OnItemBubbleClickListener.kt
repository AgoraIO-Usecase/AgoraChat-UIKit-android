package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.common.ChatMessage

interface OnItemBubbleClickListener {
    fun onBubbleClick(message: ChatMessage?)
}