package io.agora.uikit.common.extensions

import io.agora.uikit.common.ChatConversation
import io.agora.uikit.common.ChatConversationType
import io.agora.uikit.model.EaseConversation

/**
 * Convert [ChatConversation] to [EaseConversation].
 */
fun ChatConversation.parse() = EaseConversation(
    conversationId = conversationId(),
    conversationType = type,
    unreadMsgCount = unreadMsgCount,
    lastMessage = lastMessage,
    timestamp = lastMessage?.msgTime ?: 0,
    isPinned = isPinned,
    pinnedTime = pinnedTime
)

/**
 * Whether the conversation is group chat.
 */
val ChatConversation.isGroupChat:Boolean
    get() = type == ChatConversationType.GroupChat

/**
 * Whether the conversation is chat room.
 */
val ChatConversation.isChatroom:Boolean
    get() = type == ChatConversationType.ChatRoom