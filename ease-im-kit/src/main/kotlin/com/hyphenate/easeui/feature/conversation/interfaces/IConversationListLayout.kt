package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.feature.conversation.adapter.ChatUIKitConversationListAdapter
import com.hyphenate.easeui.common.interfaces.IRecyclerView
import com.hyphenate.easeui.model.ChatUIKitConversation
import com.hyphenate.easeui.viewmodel.conversations.IConversationListRequest

/**
 * Defines the conversation interface called by developers.
 */
interface IConversationListLayout: IRecyclerView {

    fun setViewModel(viewModel: IConversationListRequest?)

    /**
     * Set custom list adapter
     * @param listAdapter
     */
    fun setListAdapter(listAdapter: ChatUIKitConversationListAdapter?)

    /**
     * Get data adapter
     * @return
     */
    fun getListAdapter(): ChatUIKitConversationListAdapter?

    /**
     * Get item data
     * @param position
     * @return
     */
    fun getItem(position: Int): ChatUIKitConversation?


    /**
     * Make conversation read
     * @param position
     * @param info
     */
    fun makeConversionRead(position: Int, info: ChatUIKitConversation?)

    fun makeConversationTop(position: Int, info: ChatUIKitConversation?)

    fun cancelConversationTop(position: Int, info: ChatUIKitConversation?)

    /**
     * Delete conversation
     * @param position
     * @param info
     */
    fun deleteConversation(position: Int, info: ChatUIKitConversation?)

    /**
     * Set up monitoring of session changes
     * @param listener
     */
    fun setOnConversationChangeListener(listener: OnConversationListChangeListener?)

    /**
     * load conversation data listener
     * @param listener
     * @return
     */
    fun setLoadConversationListener(listener: OnLoadConversationListener)

}