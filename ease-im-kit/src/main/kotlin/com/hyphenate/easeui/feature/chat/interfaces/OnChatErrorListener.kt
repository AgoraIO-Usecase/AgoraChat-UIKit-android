package com.hyphenate.easeui.feature.chat.interfaces

interface OnChatErrorListener {
    /**
     * Wrong message in chat
     * @param code
     * @param errorMsg
     */
    fun onChatError(code: Int, errorMsg: String?)
}