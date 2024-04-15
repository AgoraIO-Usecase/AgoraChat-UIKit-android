package com.hyphenate.easeui.repository

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.ChatPushManager
import com.hyphenate.easeui.common.ChatPushRemindType
import com.hyphenate.easeui.common.ChatSilentModeParam
import com.hyphenate.easeui.common.ChatSilentModelType
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.common.helper.EasePreferenceManager
import com.hyphenate.easeui.common.suspends.clearSilentModeForConversation
import com.hyphenate.easeui.common.suspends.deleteConversationFromServer
import com.hyphenate.easeui.common.suspends.fetchConversationsFromServer
import com.hyphenate.easeui.common.suspends.getSilentModeOfConversations
import com.hyphenate.easeui.common.suspends.pinConversation
import com.hyphenate.easeui.common.suspends.setSilentModeForConversation
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.chatConversation
import com.hyphenate.easeui.provider.EaseGroupProfileProvider
import com.hyphenate.easeui.provider.EaseUserProfileProvider
import com.hyphenate.easeui.provider.fetchProfilesBySuspend
import com.hyphenate.easeui.provider.fetchUsersBySuspend
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
            val hasLoaded: Boolean = EasePreferenceManager.getInstance().isLoadedConversationsFromServer() ?: false
            if (hasLoaded) {
                if (EaseIM.DEBUG) {
                    ChatLog.d(TAG, "loadData from local db")
                }
                chatManager.allConversationsBySort
                    // Filter system message and empty conversations.
                    ?.filter {
                        it.conversationId() != EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID && it.allMessages.isNotEmpty()
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
                ChatSilentModeParam(ChatSilentModelType.REMIND_TYPE).setRemindType(ChatPushRemindType.NONE))
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
            EaseIM.getUserProvider()?.fetchUsersBySuspend(userList)
        }
}