package com.hyphenate.easeui.feature.chat.reaction.interfaces

import com.hyphenate.easeui.viewmodel.reaction.IReactionUserListRequest

interface IMessageReactionUsers {
    /**
     * Set custom ViewModel.
     */
    fun setViewModel(viewModel: IReactionUserListRequest?)

    /**
     * Remove a reaction from the message.
     */
    fun removeReaction(reaction: String?)

    /**
     * Fetch reaction detail of the target reaction by page.
     */
    fun fetchReactionDetail(reaction: String?)

    /**
     * Fetch more reaction detail of the target reaction by page.
     */
    fun fetchMoreReactionDetail(reaction: String?)
}