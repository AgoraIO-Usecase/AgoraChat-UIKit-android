package com.hyphenate.easeui.feature.chat.reaction.interfaces

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.viewmodel.reaction.IMessageReactionListRequest

interface IMessageReactionList {
    /**
     * Set custom ViewModel.
     */
    fun setViewModel(viewModel: IMessageReactionListRequest?)

    /**
     * Fetch reaction list of the target message.
     */
    fun fetchReactionList(message: ChatMessage)
}