package com.hyphenate.easeui.repository

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.suspends.reportChatMessage
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