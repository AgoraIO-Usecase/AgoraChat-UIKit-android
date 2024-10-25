package io.agora.uikit.feature.chat.reaction.interfaces

import io.agora.uikit.common.interfaces.IControlDataView
import io.agora.uikit.model.EaseReaction

interface IMessageReactionListResultView: IControlDataView {

    /**
     * Fetch reaction list of the target message successfully.
     */
    fun fetchReactionListSuccess(messageId: String, reactions: List<EaseReaction>?)

    /**
     * Fetch reaction list of the target message failed.
     */
    fun fetchReactionListFail(messageId: String, errorCode: Int, errorMsg: String?)

}