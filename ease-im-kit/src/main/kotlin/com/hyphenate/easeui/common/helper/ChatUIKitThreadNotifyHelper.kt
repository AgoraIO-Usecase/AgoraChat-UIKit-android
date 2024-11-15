package com.hyphenate.easeui.common.helper

import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatThreadEvent
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.ChatUIKitConstant

object ChatUIKitThreadNotifyHelper {
    fun createThreadCreatedMsg(event:ChatThreadEvent?){
        event?.let {
            var content = ""
            val message = ChatMessage.createReceiveMessage(ChatMessageType.TXT)
            message.chatType = ChatType.GroupChat
            message.from = it.from
            message.to = it.chatThread.parentId
            message.msgId = it.chatThread.chatThreadId
            message.setAttribute(ChatUIKitConstant.THREAD_NOTIFICATION_TYPE,true)
            message.setAttribute(ChatUIKitConstant.THREAD_TOPIC_MESSAGE_ID,it.chatThread.messageId)

            ChatUIKitClient.getContext()?.let { con->
                content = con.getString(R.string.uikit_thread_notify_content,"",it.chatThread.chatThreadName)
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