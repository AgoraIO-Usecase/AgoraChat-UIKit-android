package io.agora.uikit.common.helper

import io.agora.uikit.EaseIM
import io.agora.uikit.R
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageStatus
import io.agora.uikit.common.ChatMessageType
import io.agora.uikit.common.ChatTextMessageBody
import io.agora.uikit.common.ChatThreadEvent
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.EaseConstant

object EaseThreadNotifyHelper {
    fun createThreadCreatedMsg(event:ChatThreadEvent?){
        event?.let {
            var content = ""
            val message = ChatMessage.createReceiveMessage(ChatMessageType.TXT)
            message.chatType = ChatType.GroupChat
            message.from = it.from
            message.to = it.chatThread.parentId
            message.msgId = it.chatThread.chatThreadId
            message.setAttribute(EaseConstant.THREAD_NOTIFICATION_TYPE,true)
            message.setAttribute(EaseConstant.THREAD_TOPIC_MESSAGE_ID,it.chatThread.messageId)

            EaseIM.getContext()?.let { con->
                content = con.getString(R.string.ease_thread_notify_content,"",it.chatThread.chatThreadName)
            }
            message.body = ChatTextMessageBody(content)
            message.setStatus(ChatMessageStatus.SUCCESS)
            ChatClient.getInstance().chatManager().saveMessage(message)
        }
    }

    fun removeCreateThreadNotify(conversationId:String,threadId:String){
        ChatClient.getInstance().chatManager().getConversation(conversationId)?.let {
            it.removeMessage(threadId)
        }
    }
}