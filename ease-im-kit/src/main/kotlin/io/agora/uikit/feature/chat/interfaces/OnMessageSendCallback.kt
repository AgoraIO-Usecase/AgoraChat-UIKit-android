package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.common.ChatMessage

interface OnMessageSendCallback {
    /**
     * Callback after the message is sent successfully
     * @param message
     */
    fun onSuccess(message: ChatMessage?) {}

    /**
     * Wrong message in chat
     * @param code
     * @param errorMsg
     */
    fun onError(code: Int, errorMsg: String?)
}