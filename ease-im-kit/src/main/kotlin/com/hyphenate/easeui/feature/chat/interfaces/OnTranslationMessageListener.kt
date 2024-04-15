package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage

interface OnTranslationMessageListener {
    /**
     * translation message success
     */
    fun onTranslationMessageSuccess(message: ChatMessage?){}

    /**
     * hide translation message
     */
    fun onHideTranslationMessage(message: ChatMessage?){}

    /**
     * translation message failure
     * @param code
     * @param error
     */
    fun onTranslationMessageFailure(code: Int, error: String?){}
}