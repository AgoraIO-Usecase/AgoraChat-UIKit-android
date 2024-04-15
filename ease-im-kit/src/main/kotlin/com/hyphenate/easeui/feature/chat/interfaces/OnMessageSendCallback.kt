package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage

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