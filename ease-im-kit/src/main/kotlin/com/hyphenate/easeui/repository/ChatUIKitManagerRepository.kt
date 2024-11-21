package com.hyphenate.easeui.repository

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageBody
import com.hyphenate.easeui.common.ChatMessageStatus
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.ChatSearchDirection
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.ChatValueCallback
import com.hyphenate.easeui.common.ChatroomManager
import com.hyphenate.easeui.common.suspends.ackConversationToRead
import com.hyphenate.easeui.common.suspends.ackGroupMessageToRead
import com.hyphenate.easeui.common.suspends.ackMessageToRead
import com.hyphenate.easeui.common.suspends.addMessageReaction
import com.hyphenate.easeui.common.suspends.fetchHistoryMessages
import com.hyphenate.easeui.common.suspends.fetchPinChatMessageFromServer
import com.hyphenate.easeui.common.suspends.fetchReactionDetailBySuspend
import com.hyphenate.easeui.common.suspends.fetchReactionListBySuspend
import com.hyphenate.easeui.common.suspends.joinChatroom
import com.hyphenate.easeui.common.suspends.leaveChatroom
import com.hyphenate.easeui.common.suspends.modifyMessage
import com.hyphenate.easeui.common.suspends.pinChatMessage
import com.hyphenate.easeui.common.suspends.recallChatMessage
import com.hyphenate.easeui.common.suspends.removeMessageReaction
import com.hyphenate.easeui.common.suspends.reportChatMessage
import com.hyphenate.easeui.common.suspends.translationChatMessage
import com.hyphenate.easeui.common.suspends.unPinChatMessage
import com.hyphenate.easeui.common.utils.isMessageIdValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatUIKitManagerRepository(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
    private val chatroomManager: ChatroomManager = ChatClient.getInstance().chatroomManager()
) {

    suspend fun joinChatroom(roomId: String) =
        withContext(Dispatchers.IO) {
            chatroomManager.joinChatroom(roomId)
        }

    suspend fun leaveChatroom(roomId: String) =
        withContext(Dispatchers.IO) {
            chatroomManager.leaveChatroom(roomId)
        }

    suspend fun loadLocalMessages(conversation: ChatConversation?, startMsgId: String?
                                      , pageSize: Int, direction: ChatSearchDirection): List<ChatMessage> {
        return withContext(Dispatchers.IO) {
            if (conversation == null) {
                throw ChatException(ChatError.INVALID_PARAM, "Should first set up with conversation.")
            }
            if (!isMessageIdValid(startMsgId)) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Invalid message id.")
            }
            conversation.loadMoreMsgFromDB(startMsgId, pageSize, direction).map {
                if (it.status() == ChatMessageStatus.CREATE) {
                    it.setStatus(ChatMessageStatus.FAIL)
                }
                it
            }
        }
    }

    suspend fun searchMessagesByTimestamp(conversation: ChatConversation?, timestamp: Long, pageSize: Int, direction: ChatSearchDirection) =
        withContext(Dispatchers.IO) {
            if (conversation == null) {
                throw ChatException(ChatError.INVALID_PARAM, "Should first set up with conversation.")
            }
            conversation.searchMsgFromDB(timestamp, pageSize, direction).map {
                if (it.status() == ChatMessageStatus.CREATE) {
                    it.setStatus(ChatMessageStatus.FAIL)
                }
                it
            }
        }

    suspend fun fetchRoamMessages(conversation: ChatConversation?, startMsgId: String?
                                      , pageSize: Int, direction: ChatSearchDirection) =
        withContext(Dispatchers.IO) {
            if (conversation == null) {
                throw ChatException(ChatError.INVALID_PARAM, "Should first set up with conversation.")
            }
            if (!isMessageIdValid(startMsgId)) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Invalid message id.")
            }
            chatManager.fetchHistoryMessages(conversation.conversationId(), conversation.type
                , startMsgId, pageSize, direction).data
        }

    suspend fun reportMessage(tag:String,reason:String?="",msgId: String):Int =
        withContext(Dispatchers.IO) {
            chatManager.reportChatMessage(msgId,tag,reason)
        }

    suspend fun recallMessage(message: ChatMessage?) =
        withContext(Dispatchers.IO) {
            chatManager.recallChatMessage(message)
        }

    suspend fun modifyMessage(messageId: String?, messageBodyModified: ChatMessageBody?) =
        withContext(Dispatchers.IO) {
            chatManager.modifyMessage(messageId, messageBodyModified)
        }

    suspend fun addReaction(message: ChatMessage?, reaction: String?) =
        withContext(Dispatchers.IO) {
            chatManager.addMessageReaction(message?.msgId, reaction)
        }

    suspend fun removeReaction(message: ChatMessage?, reaction: String?) =
        withContext(Dispatchers.IO) {
            chatManager.removeMessageReaction(message?.msgId, reaction)
        }

    suspend fun ackConversationRead(conversationId: String?) =
        withContext(Dispatchers.IO) {
            chatManager.ackConversationToRead(conversationId)
        }

    suspend fun ackGroupMessageRead(conversationId: String?, messageId: String?, ext: String?) =
        withContext(Dispatchers.IO) {
            chatManager.ackGroupMessageToRead(conversationId, messageId, ext)
        }

    suspend fun ackMessageRead(conversationId: String?, messageId: String?) =
        withContext(Dispatchers.IO) {
            chatManager.ackMessageToRead(conversationId, messageId)
        }

    suspend fun addReaction(messageId: String?, reaction: String) =
        withContext(Dispatchers.IO) {
            if (messageId.isNullOrEmpty()) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Invalid message id.")
            }
            chatManager.addMessageReaction(messageId, reaction)
        }

    suspend fun removeReaction(messageId: String?, reaction: String?) =
        withContext(Dispatchers.IO) {
            if (messageId.isNullOrEmpty()) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Invalid message id.")
            }
            if (reaction.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "Invalid reaction.")
            }
            chatManager.removeMessageReaction(messageId, reaction)
        }

    suspend fun fetchReactionList(messageIdList: List<String>
                                  , chatType: ChatType
                                  , groupId: String? = null) =
        withContext(Dispatchers.IO) {
            chatManager.fetchReactionListBySuspend(messageIdList, chatType, groupId)
        }

    suspend fun fetchReactionDetail(messageId: String?, reaction: String?, cursor: String?, pageSize: Int) =
        withContext(Dispatchers.IO) {
            if (messageId.isNullOrEmpty()) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Invalid message id.")
            }
            if (reaction.isNullOrEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "Invalid reaction.")
            }
            chatManager.fetchReactionDetailBySuspend(messageId, reaction, cursor, pageSize)
        }

    suspend fun translationMessage(message:ChatMessage,languages:MutableList<String>) =
        withContext(Dispatchers.IO){
            chatManager.translationChatMessage(message,languages)
        }

    suspend fun downloadCombinedMessageAttachment(message: ChatMessage?, valueCallback: ChatValueCallback<List<ChatMessage>>) =
        withContext(Dispatchers.IO) {
            if (message == null) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Message cannot be null.")
            }
            if (message.type != ChatMessageType.COMBINE) {
                throw ChatException(ChatError.MESSAGE_INVALID, "Message type should be combine.")
            }
            chatManager.downloadAndParseCombineMessage(message, valueCallback)
        }

    suspend fun pinMessage(message:ChatMessage?) =
        withContext(Dispatchers.IO){
            chatManager.pinChatMessage(message?.msgId)
        }

    suspend fun unPinMessage(message:ChatMessage?) =
        withContext(Dispatchers.IO){
            chatManager.unPinChatMessage(message?.msgId)
        }

    suspend fun fetchPinMessageFromService(conversationId:String?) =
        withContext(Dispatchers.IO){
           chatManager.fetchPinChatMessageFromServer(conversationId)
        }
}