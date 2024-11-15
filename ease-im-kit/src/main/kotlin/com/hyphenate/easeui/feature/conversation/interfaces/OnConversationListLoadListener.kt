package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.model.ChatUIKitConversation

/**
 * Callback for loading conversation results.
 */
interface OnConversationListLoadListener {
    /**
     * Call back after successfully loading conversations
     * @param data
     */
    fun loadConversationsFinish(data: List<ChatUIKitConversation>)

    /**
     * Call back after failed to load conversations
     * @param code
     * @param message
     */
    fun loadConversationsFail(code: Int, message: String?) {}
}