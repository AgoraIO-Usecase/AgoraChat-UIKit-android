package com.hyphenate.easeui.feature.chat.interfaces

import com.hyphenate.easeui.common.enums.EaseChatFinishReason

interface OnChatFinishListener {
    /**
     * \~english
     * The callback that chat is finished.
     * @param reason
     * @param id
     */
    fun onChatFinish(reason: EaseChatFinishReason, id: String?)
}