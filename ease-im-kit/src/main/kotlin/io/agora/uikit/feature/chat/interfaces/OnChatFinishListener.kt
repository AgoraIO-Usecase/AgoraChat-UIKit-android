package io.agora.uikit.feature.chat.interfaces

import io.agora.uikit.common.enums.EaseChatFinishReason

interface OnChatFinishListener {
    /**
     * \~english
     * The callback that chat is finished.
     * @param reason
     * @param id
     */
    fun onChatFinish(reason: EaseChatFinishReason, id: String?)
}