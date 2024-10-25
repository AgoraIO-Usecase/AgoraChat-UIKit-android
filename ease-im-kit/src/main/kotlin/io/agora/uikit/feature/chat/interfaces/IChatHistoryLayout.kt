package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.feature.chat.adapter.EaseMessagesAdapter
import io.agora.uikit.feature.chat.widgets.EaseChatMessageListLayout
import io.agora.uikit.viewmodel.chathistory.IChatHistoryRequest

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