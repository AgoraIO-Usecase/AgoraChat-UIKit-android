package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatPresence

interface OnChatPresenceListener {

    /**
     * \~english
     * The callback that chat presence success.
     * @param presence
     */
    fun fetchChatPresenceSuccess(presence:MutableList<ChatPresence>?){}


    /**
     * \~english
     * The callback that chat presence fail.
     * @param code
     * @param error
     */
    fun fetchChatPresenceFail(code: Int, error: String?){}

}