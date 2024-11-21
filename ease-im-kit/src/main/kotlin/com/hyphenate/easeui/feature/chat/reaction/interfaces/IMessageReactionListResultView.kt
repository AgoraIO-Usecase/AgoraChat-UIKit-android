package com.hyphenate.easeui.feature.chat.reaction.interfaces

import com.hyphenate.easeui.common.interfaces.IControlDataView
import com.hyphenate.easeui.model.ChatUIKitReaction

interface IMessageReactionListResultView: IControlDataView {

    /**
     * Fetch reaction list of the target message successfully.
     */
    fun fetchReactionListSuccess(messageId: String, reactions: List<ChatUIKitReaction>?)

    /**
     * Fetch reaction list of the target message failed.
     */
    fun fetchReactionListFail(messageId: String, errorCode: Int, errorMsg: String?)

}