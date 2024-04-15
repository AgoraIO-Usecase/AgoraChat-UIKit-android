package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.model.EaseConversation

/**
 * Callback for loading conversation results.
 */
interface OnConversationListLoadListener {
    /**
     * Call back after successfully loading conversations
     * @param data
     */
    fun loadConversationsFinish(data: List<EaseConversation>)

    /**
     * Call back after failed to load conversations
     * @param code
     * @param message
     */
    fun loadConversationsFail(code: Int, message: String?) {}
}