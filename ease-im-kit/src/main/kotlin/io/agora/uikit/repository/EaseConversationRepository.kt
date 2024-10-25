package io.agora.uikit.repository

import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatConversationType
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatManager
import io.agora.uikit.common.ChatPushManager
import io.agora.uikit.common.ChatPushRemindType
import io.agora.uikit.common.ChatSilentModeParam
import io.agora.uikit.common.ChatSilentModelType
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.parse
import io.agora.uikit.common.helper.EasePreferenceManager
import io.agora.uikit.common.suspends.clearSilentModeForConversation
import io.agora.uikit.common.suspends.deleteConversationFromServer
import io.agora.uikit.common.suspends.fetchConversationsFromServer
import io.agora.uikit.common.suspends.getSilentModeOfConversations
import io.agora.uikit.common.suspends.pinConversation
import io.agora.uikit.common.suspends.setSilentModeForConversation
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.model.chatConversation
import io.agora.uikit.provider.EaseGroupProfileProvider
import io.agora.uikit.provider.EaseUserProfileProvider
import io.agora.uikit.provider.fetchProfilesBySuspend
import io.agora.uikit.provider.fetchUsersBySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EaseConversationRepository(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
    private val pushManager: ChatPushManager = ChatClient.getInstance().pushManager()
) {
    companion object {
        private const val TAG = "ConversationRep"
        private const val LIMIT = 50
    }

    /**
     * Load conversation list from local db or server.
     */
    suspend fun loadData(): List<EaseConversation> =
        withContext(Dispatchers.IO) {
            val hasLoaded: Boolean = EasePreferenceManager.getInstance().isLoadedConversationsFromServer()
            if (hasLoaded) {
                if (EaseIM.DEBUG) {
                    ChatLog.d(TAG, "loadData from local db")
                }
                chatManager.allConversationsBySort
                    // Filter system message and empty conversations.
                    ?.filter {
                        it.conversationId() != EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID
                    }
                    ?.map {
                        it.parse()
                    } ?: listOf()
            }else {
                if (EaseIM.DEBUG) {
                    ChatLog.d(TAG, "loadData from server")
                }
                var cursor: String? = null
                do {
                    val result = chatManager.fetchConversationsFromServer(LIMIT, cursor)
                    val conversations = result.data

                    try {
                        val silentResult = pushManager.getSilentModeOfConversations(conversations)
                        conversations.iterator().forEach {
                            val conversation = it.parse()
                            conversation.setSilent(silentResult[conversation.conversationId]?.isConversationRemindTypeEnabled ?: false)
                        }
                    } catch (e: Exception) {
                        ChatLog.e(TAG, "getSilentModeOfConversations error: ${e.message}")
                    }
                    cursor = result.cursor
                }while (!cursor.isNullOrEmpty())
                EasePreferenceManager.getInstance().setLoadedConversationsFromServer(true)
                chatManager.allConversationsBySort
                    // Filter system message and empty conversations.
                    ?.filter {
                        it.conversationId() != EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID
                    }
                    ?.map {
                        it.parse()
                    } ?: listOf()
            }
        }

    /**
     * Load conversation list from local db.
     */
    suspend fun loadLocalConversation() =
        withContext(Dispatchers.IO){
            val localData = chatManager.allConversationsBySort?.filter {
                it.conversationId() != EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID
            }?.map { it.parse() } ?: listOf()
            localData
        }

    /**
     * Mark conversation as read.
     */
    suspend fun makeConversionRead(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            conversation.run {
                chatConversation()?.markAllMessagesAsRead()
            }
        }

    /**
     * Make conversations interruption-free
     */
    suspend fun makeSilentForConversation(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            with(conversation) {
                val result = pushManager.setSilentModeForConversation(conversationId, conversationType,
                    ChatSilentModeParam(ChatSilentModelType.REMIND_TYPE).setRemindType(ChatPushRemindType.NONE))

                setSilent(true)
                result
            }
        }

    /**
     * Cancel conversation do not disturb
     */
    suspend fun cancelSilentForConversation(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            with(conversation) {
                val result = pushManager.clearSilentModeForConversation(conversationId, conversationType)
                setSilent(false)
                result
            }
        }

    /**
     * Make conversations interruption-free
     */
    suspend fun makeSilentForConversation(conversationId: String,conversationType:ChatConversationType) =
        withContext(Dispatchers.IO) {
            val result = pushManager.setSilentModeForConversation(conversationId, conversationType,
                ChatSilentModeParam(ChatSilentModelType.REMIND_TYPE).setRemindType(ChatPushRemindType.MENTION_ONLY))
            EaseIM.getCache().setMutedConversation(conversationId)
            result
        }

    /**
     * Cancel conversation do not disturb
     */
    suspend fun cancelSilentForConversation(conversationId: String,conversationType:ChatConversationType) =
        withContext(Dispatchers.IO) {
            val result = pushManager.clearSilentModeForConversation(conversationId, conversationType)
            EaseIM.getCache().removeMutedConversation(conversationId)
            result
        }

    /**
     * Pin conversation.
     */
    suspend fun pinConversation(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            chatManager.pinConversation(conversation.conversationId, true)
        }

    /**
     * Unpin conversation.
     */
    suspend fun unpinConversation(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            chatManager.pinConversation(conversation.conversationId, false)
        }

    /**
     * Delete conversation.
     * @param conversation The conversation to be deleted.
     * @param isDeleteLocalOnly Whether to delete the local conversation.
     */
    suspend fun deleteConversation(conversation: EaseConversation, isDeleteLocalOnly: Boolean = true) =
        withContext(Dispatchers.IO) {
            conversation.run {
                if (isDeleteLocalOnly) {
                    val result = chatManager.deleteConversation(conversationId, true)
                    if (result) {
                        ChatError.EM_NO_ERROR
                    } else {
                        ChatError.INVALID_CONVERSATION
                    }
                } else {
                    chatManager.deleteConversationFromServer(conversationId, conversationType, true)
                }
            }
        }

    /**
     * Fetch group info from [EaseGroupProfileProvider.fetchGroups]
     */
    suspend fun fetchConvGroupInfo(conversationList: List<EaseConversation>) =
        withContext(Dispatchers.IO) {
            val groupList = conversationList
                .filter {
                    it.conversationType == ChatConversationType.GroupChat &&
                            EaseIM.getCache().getGroup(it.conversationId) == null
                }.map {
                    it.conversationId
                }
            EaseIM.getGroupProfileProvider()?.fetchProfilesBySuspend(groupList)
        }

    /**
     * Fetch user info from [EaseUserProfileProvider.fetchUsers]
     */
    suspend fun fetchConvUserInfo(conversationList: List<EaseConversation>) =
        withContext(Dispatchers.IO) {
            val userList = conversationList
                .filter {
                    it.conversationType == ChatConversationType.Chat &&
                            EaseIM.getCache().getUser(it.conversationId) == null &&
                            EaseIM.getCache().getMessageUserInfo(it.conversationId) == null
                }.map {
                    it.conversationId
                }
            if (userList.isEmpty()) {
                throw ChatException(ChatError.INVALID_PARAM, "userList is empty.")
            }
            EaseIM.getUserProvider()?.fetchUsersBySuspend(userList)
        }

    /**
     * Clear All Conversation Message
     */
    suspend fun clearConversationMessage(conversation: EaseConversation) =
        withContext(Dispatchers.IO) {
            conversation.run {
                conversation.chatConversation()?.clearAllMessages()
                ChatError.EM_NO_ERROR
            }
    }
}