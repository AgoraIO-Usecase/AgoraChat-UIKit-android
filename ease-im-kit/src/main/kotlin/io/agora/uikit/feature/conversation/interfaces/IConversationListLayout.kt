package io.agora.uikit.feature.conversation.interfaces

import io.agora.uikit.feature.conversation.adapter.EaseConversationListAdapter
import io.agora.uikit.common.interfaces.IRecyclerView
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.viewmodel.conversations.IConversationListRequest

/**
 * Defines the conversation interface called by developers.
 */
interface IConversationListLayout: IRecyclerView {

    fun setViewModel(viewModel: IConversationListRequest?)

    /**
     * Set custom list adapter
     * @param listAdapter
     */
    fun setListAdapter(listAdapter: EaseConversationListAdapter?)

    /**
     * Get data adapter
     * @return
     */
    fun getListAdapter(): EaseConversationListAdapter?

    /**
     * Get item data
     * @param position
     * @return
     */
    fun getItem(position: Int): EaseConversation?


    /**
     * Make conversation read
     * @param position
     * @param info
     */
    fun makeConversionRead(position: Int, info: EaseConversation?)

    fun makeConversationTop(position: Int, info: EaseConversation?)

    fun cancelConversationTop(position: Int, info: EaseConversation?)

    /**
     * Delete conversation
     * @param position
     * @param info
     */
    fun deleteConversation(position: Int, info: EaseConversation?)

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