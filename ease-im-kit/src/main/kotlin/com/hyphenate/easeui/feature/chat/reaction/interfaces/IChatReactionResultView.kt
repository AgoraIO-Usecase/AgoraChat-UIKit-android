package com.hyphenate.easeui.feature.chat.reaction.interfaces

import com.hyphenate.easeui.common.interfaces.IControlDataView
import com.hyphenate.easeui.model.EaseReaction

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