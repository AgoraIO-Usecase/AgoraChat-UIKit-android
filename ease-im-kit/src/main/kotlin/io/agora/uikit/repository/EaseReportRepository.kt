package io.agora.uikit.repository

import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatManager
import io.agora.uikit.common.suspends.reportChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseReportRepository(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
) {
    suspend fun reportMessage(tag:String,msgId: String,reason:String?=""):Int =
        withContext(Dispatchers.IO) {
            chatManager.reportChatMessage(msgId,tag,reason)
        }

}