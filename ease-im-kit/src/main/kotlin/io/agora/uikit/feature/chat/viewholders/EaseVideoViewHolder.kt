package io.agora.uikit.feature.chat.viewholders

import android.view.View
import io.agora.uikit.feature.chat.activities.EaseShowVideoActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatDownloadStatus
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageDirection
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.ChatVideoMessageBody

class EaseVideoViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        message?.run {
            if (body !is ChatVideoMessageBody) {
                ChatLog.e(TAG, "message body is not video type")
                return
            }
            if (ChatClient.getInstance().options.autodownloadThumbnail) {

            } else {
                val body: ChatVideoMessageBody = body as ChatVideoMessageBody
                if (body.thumbnailDownloadStatus() === ChatDownloadStatus.DOWNLOADING
                    || body.thumbnailDownloadStatus() === ChatDownloadStatus.PENDING
                    || body.thumbnailDownloadStatus() === ChatDownloadStatus.FAILED) {
                    // retry download with click event of user
                    ChatClient.getInstance().chatManager().downloadThumbnail(this)
                    return
                }
            }
            if (direct() === ChatMessageDirection.RECEIVE && !isAcked && chatType === ChatType.Chat) {
                try {
                    ChatClient.getInstance().chatManager()
                        .ackMessageRead(from, msgId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            EaseShowVideoActivity.actionStart(mContext, message)
        }

    }

    companion object {
        private val TAG = EaseVideoViewHolder::class.java.simpleName
    }
}