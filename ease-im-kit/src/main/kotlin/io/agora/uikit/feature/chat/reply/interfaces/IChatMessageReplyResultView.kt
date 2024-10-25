package io.agora.uikit.feature.chat.reply.interfaces

import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.interfaces.IControlDataView

interface IChatMessageReplyResultView : IControlDataView {
    /**
     * Show nickname.
     * @param nickname
     */
    fun showQuoteMessageNickname(nickname: String?)

    /**
     * Show content.
     * @param content
     */
    fun showQuoteMessageContent(content: CharSequence?)

    /**
     * Show attachment.
     * @param localPath
     * @param remotePath
     * @param defaultResource
     */
    fun showQuoteMessageAttachment(
        type: ChatMessageType?,
        localPath: String?,
        remotePath: String?,
        defaultResource: Int
    )

    /**
     * Show error message.
     * @param code
     * @param message
     */
    fun onShowError(code: Int, message: String?)
}