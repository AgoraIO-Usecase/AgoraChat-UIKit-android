package io.agora.uikit.feature.conversation.interfaces

import io.agora.uikit.common.interfaces.IControlDataView
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.model.EaseProfile

/**
 * Conversation list result interface by [EaseConversationListViewModel]
 */
interface IEaseConvListResultView: IControlDataView {

    /**
     * Load conversation list successfully.
     */
    fun loadConversationListSuccess(list: List<EaseConversation>)

    /**
     * Load conversation list failed.
     */
    fun loadConversationListFail(code: Int, error: String)

    /**
     * Sort conversation list successfully.
     */
    fun sortConversationListFinish(conversations: List<EaseConversation>)

    /**
     * Mark conversation read successfully.
     */
    fun makeConversionReadSuccess(position: Int, conversation: EaseConversation)

    /**
     * Pin conversation successfully.
     */
    fun pinConversationSuccess(position: Int, conversation: EaseConversation)

    /**
     * Pin conversation failed.
     */
    fun pinConversationFail(conversation: EaseConversation, code: Int, error: String)

    /**
     * Unpin conversation successfully.
     */
    fun unpinConversationSuccess(position: Int, conversation: EaseConversation)

    /**
     * Unpin conversation failed.
     */
    fun unpinConversationFail(conversation: EaseConversation, code: Int, error: String)

    /**
     * Delete conversation successfully.
     */
    fun deleteConversationSuccess(position: Int, conversation: EaseConversation)

    /**
     * Delete conversation failed.
     */
    fun deleteConversationFail(conversation: EaseConversation, code: Int, error: String)

    /**
     * Make conversations interruption-free successfully.
     */
    fun makeSilentForConversationSuccess(position: Int, conversation: EaseConversation)

    /**
     * Make conversations interruption-free failed.
     */
    fun makeSilentForConversationFail(
        conversation: EaseConversation,
        errorCode: Int,
        description: String?
    )

    /**
     * Cancel conversation do not disturb successfully.
     */
    fun cancelSilentForConversationSuccess(position: Int, conversation: EaseConversation)

    /**
     * Cancel conversation do not disturb failed.
     */
    fun cancelSilentForConversationFail(
        conversation: EaseConversation,
        errorCode: Int,
        description: String?
    )

    /**
     * Fetch conversation info from user successfully.
     */
    fun fetchConversationInfoByUserSuccess(profiles: List<EaseProfile>?)

}