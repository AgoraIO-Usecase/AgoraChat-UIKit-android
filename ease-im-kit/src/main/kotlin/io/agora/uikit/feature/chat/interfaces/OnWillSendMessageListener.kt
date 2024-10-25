package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.common.ChatMessage

interface OnWillSendMessageListener {
    /**
     * Set the message properties before sending the message, such as setting ext
     * @param message
     * @return
     */
    fun onWillSendMessage(message: ChatMessage?)
}