package com.hyphenate.easeui.model

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import java.io.Serializable

/**
 * The class is used to display the conversation information.
 * @param conversationId The conversation id.
 * @param conversationType The conversation type.
 * @param unreadMsgCount The unread message count.
 * @param lastMessage The last message.
 * @param timestamp The last message timestamp.
 * @param isPinned The conversation is pinned or not.
 * @param pinnedTime The pinned time.
 */
data class EaseConversation(
    val conversationId: String,
    val conversationType: ChatConversationType,
    val unreadMsgCount: Int,
    val lastMessage: ChatMessage?,
    val timestamp: Long,
    val isPinned: Boolean,
    val pinnedTime: Long,
): Serializable, Comparable<EaseConversation> {
    private var isSelected: Boolean = false
    private var onSelectedListener: ((Boolean) -> Unit)? = null

    /**
     * Get whether the conversation is silent.
     * @return The result of whether the conversation is silent.
     */
    fun isSilent(): Boolean {
        return EaseIM.getCache().getMutedConversationList().containsKey(conversationId)
    }

    /**
     * Set whether the conversation is silent. It will update to local db.
     * @param isSilent
     */
    fun setSilent(isSilent: Boolean) {
        if (isSilent) {
            EaseIM.getCache().setMutedConversation(conversationId)
        } else {
            EaseIM.getCache().removeMutedConversation(conversationId)
        }
    }

    fun isSelected(): Boolean {
        return isSelected
    }

    fun setSelected(isSelected: Boolean) {
        this.isSelected = isSelected
        onSelectedListener?.invoke(isSelected)
    }

    /**
     * Set the listener of conversation selected event.
     */
    internal fun setOnSelectedListener(onSelected: (Boolean) -> Unit) {
        this.onSelectedListener = onSelected
    }

    override fun compareTo(other: EaseConversation): Int {
        return if (other.isPinned && !isPinned) {
            1
        } else if (!other.isPinned && isPinned) {
            -1
        } else {
            (other.timestamp - timestamp).toInt()
        }
    }
}

/**
 * Get the bean of [ChatConversation] by conversation id.
 */
fun EaseConversation.chatConversation(): ChatConversation? = ChatClient.getInstance().chatManager().getConversation(conversationId)

/**
 * Get chat type from conversation type.
 */
fun EaseConversation.getChatType(): EaseChatType {
    val conversation = chatConversation()
    return if (chatConversation()?.isGroup == true) {
        if (conversation?.type === ChatConversationType.ChatRoom) {
            EaseChatType.CHATROOM
        } else {
            EaseChatType.GROUP_CHAT
        }
    } else {
        EaseChatType.SINGLE_CHAT
    }
}

fun EaseConversation.isGroupChat() = conversationType == ChatConversationType.GroupChat

fun EaseConversation.isChatRoom() = conversationType == ChatConversationType.ChatRoom

fun EaseConversation.isChat() = conversationType == ChatConversationType.Chat
