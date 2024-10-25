package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.common.ChatMessage

interface OnMessageForwardCallback {
    /**
     * Callback after the message is sent successfully
     * @param message
     */
    fun onForwardSuccess(message: ChatMessage?) {}

    /**
     * Wrong message in chat
     * @param code
     * @param errorMsg
     */
    fun onForwardError(code: Int, errorMsg: String?)
}