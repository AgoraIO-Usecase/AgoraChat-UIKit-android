package com.hyphenate.easeui.repository

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatThreadManager
import com.hyphenate.easeui.common.suspends.createThread
import com.hyphenate.easeui.common.suspends.destroyThread
import com.hyphenate.easeui.common.suspends.getJoinedThreadsFromServer
import com.hyphenate.easeui.common.suspends.getThreadFromServer
import com.hyphenate.easeui.common.suspends.getThreadLatestMessage
import com.hyphenate.easeui.common.suspends.getThreadMembers
import com.hyphenate.easeui.common.suspends.getThreadsFromServer
import com.hyphenate.easeui.common.suspends.joinThread
import com.hyphenate.easeui.common.suspends.leaveThread
import com.hyphenate.easeui.common.suspends.removeMemberFromThread
import com.hyphenate.easeui.common.suspends.updateThreadName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseChatThreadRepository(
    private val threadManager: ChatThreadManager = ChatClient.getInstance().chatThreadManager(),
) {

    suspend fun createChatThread(
        parentId:String,
        msgId:String,
        chatThreadName:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.createThread(parentId,msgId, chatThreadName)
    }

    suspend fun fetchChatThreadFromServer(
        chatThreadId:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.getThreadFromServer(chatThreadId)
    }

    suspend fun fetchChatThreadsFromServer(
        parentId: String,
        limit: Int,
        cursor: String
    )= withContext(Dispatchers.IO) {
        threadManager.getThreadsFromServer(parentId, limit, cursor)
    }

    suspend fun joinChatThread(
        chatThreadId:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.joinThread(chatThreadId)
    }


    suspend fun destroyChatThread(
        chatThreadId:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.destroyThread(chatThreadId)
    }

    suspend fun leaveChatThread(
        chatThreadId:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.leaveThread(chatThreadId)
    }

    suspend fun updateChatThreadName(
        chatThreadId:String,
        chatThreadName:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.updateThreadName(chatThreadId,chatThreadName)
    }

    suspend fun removeMemberFromChatThread(
        chatThreadId:String,
        member:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.removeMemberFromThread(chatThreadId,member)
    }

    suspend fun getChatThreadMembers(
        chatThreadId:String,
        limit:Int,
        cursor:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.getThreadMembers(chatThreadId,limit,cursor)
    }

    suspend fun getJoinedChatThreadsFromServer(
        parentId:String? = null,
        limit:Int,
        cursor:String,
    ) = withContext(Dispatchers.IO) {
        threadManager.getJoinedThreadsFromServer(parentId,limit,cursor)
    }


    suspend fun getChatThreadLatestMessage(
        chatThreadIds:List<String>,
    ) = withContext(Dispatchers.IO) {
        threadManager.getThreadLatestMessage(chatThreadIds)
    }

}