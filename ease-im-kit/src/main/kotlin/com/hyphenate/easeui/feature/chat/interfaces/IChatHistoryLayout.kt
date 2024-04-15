package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.feature.chat.adapter.EaseMessagesAdapter
import com.hyphenate.easeui.feature.chat.widgets.EaseChatMessageListLayout
import com.hyphenate.easeui.viewmodel.chathistory.IChatHistoryRequest

interface IChatHistoryLayout {
    /**
     * set custom ViewModel
     * @param viewModel
     */
    fun setViewModel(viewModel: IChatHistoryRequest?)

    /**
     * Set custom adapter.
     */
    fun setMessagesAdapter(adapter: EaseMessagesAdapter?)

    /**
     * Set the combine message download or parse callback.
     */
    fun setOnCombineMessageDownloadCallback(callback: OnCombineMessageDownloadCallback?)

    /**
     * Get the chat message list layout.
     */
    fun getChatMessageListLayout(): EaseChatMessageListLayout
}