package com.hyphenate.easeui.viewmodel.conversations

import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.viewmodel.IAttachView

/**
 * Interface that [EaseConversationListViewModel] needs to implement
 */
interface IConversationListRequest: IAttachView {

    /**
     * load conversations from local or server.
     */
    fun loadData()

    /**
     * Sort conversations.
     */
    fun sortConversationList(conversations: List<EaseConversation>)

    /**
     * Mark conversation read
     * @param position
     * @param conversation
     */
    fun makeConversionRead(position: Int, conversation: EaseConversation)

    /**
     * Make conversations interruption-free
     */
    fun makeSilentForConversation(position: Int, conversation: EaseConversation)

    /**
     * Cancel conversation do not disturb
     */
    fun cancelSilentForConversation(position: Int, conversation: EaseConversation)

    /**
     * Pin conversation
     * @param position
     * @param conversation
     */
    fun pinConversation(position: Int, conversation: EaseConversation)

    /**
     * Unpin conversation
     * @param position
     * @param conversation
     */
    fun unpinConversation(position: Int, conversation: EaseConversation)

    /**
     * Delete conversation
     * @param position
     * @param conversation
     */
    fun deleteConversation(position: Int, conversation: EaseConversation)

    /**
     * Fetch group info.
     */
    fun fetchConvGroupInfo(conversationList: List<EaseConversation>)

    /**
     * Fetch user info.
     */
    fun fetchConvUserInfo(conversationList: List<EaseConversation>)
}