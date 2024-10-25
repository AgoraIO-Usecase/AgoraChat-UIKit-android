package io.agora.uikit.feature.chat.viewholders

import android.net.Uri
import android.view.View
import io.agora.uikit.feature.chat.activities.EaseShowNormalFileActivity
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageDirection
import io.agora.uikit.common.ChatNormalFileMessageBody
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.utils.EaseCompat
import io.agora.uikit.common.utils.EaseFileUtils

class EaseFileViewHolder(itemView: View) : EaseChatRowViewHolder(itemView) {
    override fun onBubbleClick(message: ChatMessage?) {
        super.onBubbleClick(message)
        val fileMessageBody = message?.body as ChatNormalFileMessageBody
        val filePath: Uri? = fileMessageBody.localUri
        EaseFileUtils.takePersistableUriPermission(mContext, filePath)
        if (EaseFileUtils.isFileExistByUri(mContext, filePath)) {
            EaseCompat.openFile(mContext, filePath!!)
        } else {
            // download the file
            EaseShowNormalFileActivity.actionStart(mContext, message)
        }
        message?.run {
            if(direct() == ChatMessageDirection.RECEIVE && !isAcked && chatType == ChatType.Chat) {
                try {
                    ChatClient.getInstance().chatManager()
                        .ackMessageRead(from, msgId)
                } catch (e: ChatException) {
                    e.printStackTrace()
                }
            }
        }
    }
}