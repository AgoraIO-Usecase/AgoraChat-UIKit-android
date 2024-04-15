package com.hyphenate.easeui.viewmodel.reply

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.viewmodel.IAttachView

interface IChatMessageReplyRequest: IAttachView {
    /**
     * Show reply message info.
     * @param message
     */
    fun showQuoteMessageInfo(message: ChatMessage?)
}