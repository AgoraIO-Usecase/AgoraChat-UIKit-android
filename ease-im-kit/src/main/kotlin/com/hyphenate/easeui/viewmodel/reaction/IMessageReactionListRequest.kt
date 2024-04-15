package com.hyphenate.easeui.viewmodel.reaction

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.viewmodel.IAttachView

interface IMessageReactionListRequest: IAttachView {

    /**
     * Fetch reaction list of the target message.
     */
    fun fetchReactionList(message: ChatMessage)
}