package io.agora.uikit.repository

import android.content.Context
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.suspends.agreeInviteAction
import io.agora.uikit.common.suspends.getAllSystemMessage
import io.agora.uikit.common.suspends.refuseInviteAction
import io.agora.uikit.feature.invitation.helper.EaseNotificationMsgManager
import io.agora.uikit.provider.fetchUsersBySuspend
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
                throw ChatException(ChatError.INVALID_PARAM, "members is null or empty.")
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