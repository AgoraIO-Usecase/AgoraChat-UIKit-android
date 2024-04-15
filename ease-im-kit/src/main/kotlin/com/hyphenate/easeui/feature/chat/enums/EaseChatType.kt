package com.hyphenate.easeui.feature.chat.enums

import com.hyphenate.easeui.common.ChatConversationType

enum class EaseChatType {
    /**
     * Single chat type.
     */
    SINGLE_CHAT,

    /**
     * Group chat type.
     */
    GROUP_CHAT,

    /**
     * Chat room type.
     */
    CHATROOM
}

fun EaseChatType.getConversationType(): ChatConversationType {
    return when (this) {
        EaseChatType.GROUP_CHAT -> ChatConversationType.GroupChat
        EaseChatType.CHATROOM -> ChatConversationType.ChatRoom
        else -> ChatConversationType.Chat
    }
}