package io.agora.uikit.common.suspends

import android.content.Context
import io.agora.uikit.R
import io.agora.uikit.common.ChatCallback
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatTextMessageBody
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.feature.invitation.helper.EaseNotificationMsgManager
import io.agora.uikit.feature.invitation.enums.InviteMessageStatus
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun EaseNotificationMsgManager.getAllSystemMessage():List<ChatMessage>{
    return suspendCoroutine{ continuation ->
        val allMessage = getAllNotifyMessage()
        if (allMessage.isNotEmpty()){
            continuation.resume(allMessage)
        }else{
            continuation.resume(mutableListOf())
        }
    }
}

suspend fun EaseNotificationMsgManager.loadMoreMessage(startMsgId:String,limit:Int):List<ChatMessage>{
    return suspendCoroutine{ continuation ->
        val allMessage = loadMoreMessage(startMsgId,limit)
        if (allMessage.isNotEmpty()){
            continuation.resume(allMessage)
        }else{
            continuation.resume(mutableListOf())
        }
    }
}


suspend fun EaseNotificationMsgManager.agreeInviteAction(context: Context, msg:ChatMessage):Int{
    return suspendCoroutine{ continuation ->
        try {
            val statusParams = msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS)
            val status = InviteMessageStatus.valueOf(statusParams)
            var message = ""
            when(status){
                InviteMessageStatus.BEINVITEED -> {
                    message = context.resources.getString(R.string.ease_invitation_reason)
                    ChatClient.getInstance().contactManager().asyncAcceptInvitation(
                        msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM),
                        object : ChatCallback{
                            override fun onSuccess() {
                                continuation.resume(ChatError.EM_NO_ERROR)
                            }

                            override fun onError(code: Int, error: String?) {
                                continuation.resumeWithException(ChatException(code,error))
                            }
                        })
                }
                else -> {}
            }

            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.AGREED.name)
            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_REASON, message)
            val body = ChatTextMessageBody(message)
            msg.body = body
            EaseNotificationMsgManager.getInstance().updateMessage(msg)
        }catch (e:ChatException){
            e.printStackTrace()
            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_EXPIRED, R.string.system_msg_expired)
        }

    }
}

suspend fun EaseNotificationMsgManager.refuseInviteAction(context: Context, msg:ChatMessage):Int{
    return suspendCoroutine{ continuation ->
        try {
            val statusParams = msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS)
            val status = InviteMessageStatus.valueOf(statusParams)
            var message = ""
            when(status) {
                InviteMessageStatus.BEINVITEED -> {
                    message = context.resources.getString(R.string.system_decline_invite)
                    ChatClient.getInstance().contactManager().asyncDeclineInvitation(
                        msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM),
                        object : ChatCallback{
                            override fun onSuccess() {
                                continuation.resume(ChatError.EM_NO_ERROR)
                            }

                            override fun onError(code: Int, error: String?) {
                                continuation.resumeWithException(ChatException(code,error))
                            }
                        }
                    )
                }
                else -> {}
            }
            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_STATUS, InviteMessageStatus.AGREED.name)
            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_REASON, message)
            val body = ChatTextMessageBody(message)
            msg.body = body
            EaseNotificationMsgManager.getInstance().updateMessage(msg)
        }catch (e:ChatException){
            e.printStackTrace()
            msg.setAttribute(EaseConstant.SYSTEM_MESSAGE_EXPIRED, R.string.system_msg_expired)
        }
    }
}