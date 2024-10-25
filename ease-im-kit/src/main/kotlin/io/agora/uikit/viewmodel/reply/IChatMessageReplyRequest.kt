package io.agora.uikit.viewmodel.reply

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.viewmodel.IAttachView

interface IChatMessageReplyRequest: IAttachView {
    /**
     * Show reply message info.
     * @param message
     */
    fun showQuoteMessageInfo(message: ChatMessage?)
}