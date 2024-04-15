package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage

interface OnItemBubbleClickListener {
    fun onBubbleClick(message: ChatMessage?)
}