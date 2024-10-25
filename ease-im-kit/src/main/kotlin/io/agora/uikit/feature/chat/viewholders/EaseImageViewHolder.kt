package io.agora.uikit.feature.chat.viewholders

import android.net.Uri
import android.view.View
import io.agora.uikit.EaseIM
import io.agora.uikit.feature.chat.activities.EaseShowBigImageActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatImageMessageBody
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageDirection
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.utils.EaseFileUtils

class EaseImageViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        val imgBody: ChatImageMessageBody? = message?.body as? ChatImageMessageBody
        if (EaseIM.getConfig()?.chatConfig?.enableSendChannelAck == false) {
            //Here no longer send read_ack message separately, instead enter the chat page to send channel_ack
            //New messages are sent in the onReceiveMessage method of the chat page, except for video
            // , voice and file messages, and send read_ack messages
            message?.let {
                if (it.direct() === ChatMessageDirection.RECEIVE && !it.isAcked && it.chatType === ChatType.Chat) {
                    try {
                        ChatClient.getInstance().chatManager()
                            .ackMessageRead(it.from, it.msgId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

        }
        val imgUri: Uri? = imgBody?.getLocalUri()
        EaseFileUtils.takePersistableUriPermission(mContext, imgUri)
        ChatLog.e(
            "Tag",
            "big image uri: " + imgUri + "  exist: " + EaseFileUtils.isFileExistByUri(
                mContext,
                imgUri
            )
        )
        if (EaseFileUtils.isFileExistByUri(mContext, imgUri)) {
            EaseShowBigImageActivity.actionStart(mContext, imgUri)
        } else {
            // The local full size pic does not exist yet.
            // ShowBigImage needs to download it from the server
            // first
            EaseShowBigImageActivity.actionStart(mContext, message?.msgId, imgBody?.fileName)
        }
    }

    override fun handleReceiveMessage(message: ChatMessage?) {
        super.handleReceiveMessage(message)
        getChatRow()?.updateView()
    }
}