package com.hyphenate.easeui.feature.chat.reply.interfaces

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.viewmodel.reply.ChatUIKitMessageReplyViewModel

interface IChatMessageReply {
    /**
     * Set quote message.
     * @param message
     */
    fun startQuote(message: ChatMessage?)

    /**
     * Hide quote view.
     */
    fun hideQuoteView()

    /**
     * Set quote message viewModel.
     * @param viewModel
     */
    fun setViewModel(viewModel: ChatUIKitMessageReplyViewModel?)
}