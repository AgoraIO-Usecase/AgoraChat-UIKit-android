package io.agora.uikit.feature.chat.reaction.interfaces

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.viewmodel.reaction.IMessageReactionListRequest

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