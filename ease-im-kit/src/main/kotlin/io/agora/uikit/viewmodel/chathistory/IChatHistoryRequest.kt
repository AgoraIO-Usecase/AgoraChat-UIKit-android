package io.agora.uikit.viewmodel.chathistory

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.viewmodel.IAttachView

interface IChatHistoryRequest: IAttachView {

    /**
     * Download combine message attachment.
     */
    fun downloadCombineMessage(message: ChatMessage?)

}