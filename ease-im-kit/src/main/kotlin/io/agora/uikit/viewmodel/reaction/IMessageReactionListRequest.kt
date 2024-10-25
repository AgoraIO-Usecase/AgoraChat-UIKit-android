package io.agora.uikit.viewmodel.reaction

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.viewmodel.IAttachView

interface IMessageReactionListRequest: IAttachView {

    /**
     * Fetch reaction list of the target message.
     */
    fun fetchReactionList(message: ChatMessage)
}