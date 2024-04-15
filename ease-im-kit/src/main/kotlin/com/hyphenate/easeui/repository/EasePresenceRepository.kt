package com.hyphenate.easeui.repository

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatPresenceManager
import com.hyphenate.easeui.common.suspends.fetchUserPresenceStatus
import com.hyphenate.easeui.common.suspends.publishExtPresence
import com.hyphenate.easeui.common.suspends.subscribeUsersPresence
import com.hyphenate.easeui.common.suspends.unSubscribeUsersPresence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EasePresenceRepository(
    private val presenceManager: ChatPresenceManager = ChatClient.getInstance().presenceManager(),
) {

    suspend fun publishPresence(customStatus: String) =
        withContext(Dispatchers.IO) {
            presenceManager.publishExtPresence(customStatus)
        }


    suspend fun subscribePresences(userIds:MutableList<String>,expiry:Long) =
        withContext(Dispatchers.IO) {
            presenceManager.subscribeUsersPresence(userIds,expiry)
        }


    suspend fun unSubscribePresences(userIds:MutableList<String>) =
        withContext(Dispatchers.IO) {
            presenceManager.unSubscribeUsersPresence(userIds)
        }

    suspend fun fetchPresenceStatus(userIds:MutableList<String>) =
        withContext(Dispatchers.IO) {
            presenceManager.fetchUserPresenceStatus(userIds)
        }


}