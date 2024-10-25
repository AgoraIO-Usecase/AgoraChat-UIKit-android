package io.agora.uikit.feature.conversation.interfaces

import io.agora.uikit.model.EaseConversation

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