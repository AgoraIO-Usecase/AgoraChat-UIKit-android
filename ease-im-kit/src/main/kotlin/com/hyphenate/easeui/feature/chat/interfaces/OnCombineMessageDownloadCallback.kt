package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.ChatMessage

/**
 * Download or parse combine message callback.
 */
interface OnCombineMessageDownloadCallback {

    /**
     * Download or parse combine message successfully.
     * @param messages   Parsed message list from combine message attachment.
     */
    fun onDownloadSuccess(messages: List<ChatMessage>)


    /**
     * Failed to download or parse combine message.
     * @param error    Error code.
     * @param errorMsg Error message.
     */
    fun onDownloadFail(error: Int, errorMsg: String?)
}