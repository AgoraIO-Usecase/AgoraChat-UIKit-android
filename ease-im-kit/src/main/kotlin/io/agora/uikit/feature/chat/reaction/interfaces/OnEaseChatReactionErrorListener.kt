package io.agora.uikit.feature.chat.reaction.interfaces

/**
 * The reaction calls error listener.
 */
interface OnEaseChatReactionErrorListener {

    /**
     * Callback when error occurs.
     * @param messageId the message id.
     * @param errorCode the error code.
     * @param errorMessage the error message.
     */
    fun onError(messageId: String, errorCode: Int, errorMessage: String?)

}