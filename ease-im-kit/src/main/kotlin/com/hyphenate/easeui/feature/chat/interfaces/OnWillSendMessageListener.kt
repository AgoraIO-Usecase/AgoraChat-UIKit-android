package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage

interface OnWillSendMessageListener {
    /**
     * Set the message properties before sending the message, such as setting ext
     * @param message
     * @return
     */
    fun onWillSendMessage(message: ChatMessage?)
}