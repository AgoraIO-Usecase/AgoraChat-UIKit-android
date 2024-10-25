package io.agora.uikit.feature.chat.reaction.interfaces

import io.agora.uikit.common.interfaces.IControlDataView
import io.agora.uikit.model.EaseReaction

interface IChatReactionResultView: IControlDataView {

    /**
     * Get default reactions in message menu dialog successfully.
     */
    fun getDefaultReactionsSuccess(reactions: List<EaseReaction>) {}

    /**
     * Get all chat default reactions successfully.
     */
    fun getAllChatReactionsSuccess(reactions: List<EaseReaction>) {}

    /**
     * Get message reactions successfully.
     */
    fun getMessageReactionSuccess(reactions: List<EaseReaction>) {}

    /**
     * Add a reaction to the message successfully.
     */
    fun addReactionSuccess(messageId: String) {}

    /**
     * Add a reaction to the message failed.
     */
    fun addReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {}

    /**
     * Remove a reaction from the message successfully.
     */
    fun removeReactionSuccess(messageId: String) {}

    /**
     * Remove a reaction from the message failed.
     */
    fun removeReactionFail(messageId: String, errorCode: Int, errorMsg: String?) {}

}