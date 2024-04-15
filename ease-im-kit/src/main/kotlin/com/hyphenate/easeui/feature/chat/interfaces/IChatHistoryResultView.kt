package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.interfaces.IControlDataView

interface IChatHistoryResultView: IControlDataView {
    /**
     * Download combine message successfully.
     * @param messageList   Parsed message list.
     */
    fun downloadCombinedMessagesSuccess(messageList: List<ChatMessage>)

    /**
     * Download or parse combine message failed.
     * @param error
     * @param errorMsg
     */
    fun downloadCombinedMessagesFail(error: Int, errorMsg: String?)
}