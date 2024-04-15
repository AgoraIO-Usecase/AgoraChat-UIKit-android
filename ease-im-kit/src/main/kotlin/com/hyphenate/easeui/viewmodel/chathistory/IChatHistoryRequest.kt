package com.hyphenate.easeui.viewmodel.chathistory

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.viewmodel.IAttachView

interface IChatHistoryRequest: IAttachView {

    /**
     * Download combine message attachment.
     */
    fun downloadCombineMessage(message: ChatMessage?)

}