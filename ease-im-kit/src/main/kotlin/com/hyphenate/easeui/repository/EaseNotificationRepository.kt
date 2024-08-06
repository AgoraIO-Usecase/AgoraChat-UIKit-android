package com.hyphenate.easeui.repository

import android.content.Context
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.suspends.agreeInviteAction
import com.hyphenate.easeui.common.suspends.getAllSystemMessage
import com.hyphenate.easeui.common.suspends.refuseInviteAction
import com.hyphenate.easeui.feature.invitation.helper.EaseNotificationMsgManager
import com.hyphenate.easeui.provider.fetchUsersBySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseNotificationRepository(
    private val notificationMsgManager: EaseNotificationMsgManager = EaseNotificationMsgManager.getInstance()
) {

    companion object {
        private const val TAG = "NotificationRep"
    }

    suspend fun getAllMessage():List<ChatMessage> =
        withContext(Dispatchers.IO){
            notificationMsgManager.getAllSystemMessage()
        }

    suspend fun loadMoreMessage(startMsgId:String?,limit:Int):List<ChatMessage> =
        withContext(Dispatchers.IO){
            notificationMsgManager.loadMoreMessage(startMsgId,limit)
        }

    suspend fun fetchProfileInfo(members: List<String>?) =
        withContext(Dispatchers.IO){
            if (members.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "members is null.")
            }
            EaseIM.getUserProvider()?.fetchUsersBySuspend(members)
        }

    suspend fun agreeInvite(context: Context,msg:ChatMessage):Int =
        withContext(Dispatchers.IO){
            notificationMsgManager.agreeInviteAction(context,msg)
        }

    suspend fun refuseInvite(context: Context,msg:ChatMessage):Int =
        withContext(Dispatchers.IO){
            notificationMsgManager.refuseInviteAction(context,msg)
        }

    suspend fun removeInviteMsg(msg: ChatMessage):Boolean =
        withContext(Dispatchers.IO){
            notificationMsgManager.removeMessage(msg)
        }
}