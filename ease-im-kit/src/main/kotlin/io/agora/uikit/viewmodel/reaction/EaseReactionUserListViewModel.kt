package io.agora.uikit.viewmodel.reaction

import androidx.lifecycle.viewModelScope
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.catchChatException
import io.agora.uikit.common.extensions.getParentId
import io.agora.uikit.common.extensions.isSingleChat
import io.agora.uikit.common.extensions.toUser
import io.agora.uikit.feature.chat.reaction.interfaces.IReactionUserListResultView
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser
import io.agora.uikit.model.getMessageUser
import io.agora.uikit.repository.EaseChatManagerRepository
import io.agora.uikit.viewmodel.EaseBaseViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class EaseReactionUserListViewModel: EaseBaseViewModel<IReactionUserListResultView>(), IReactionUserListRequest {
    private val chatRepository by lazy { EaseChatManagerRepository() }
    private var nextCursor: String? = null

    override fun removeReaction(message: ChatMessage, reaction: String?) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.removeReaction(message.msgId, reaction))
            }
                .catchChatException { e->
                    view?.removeReactionFail(message.msgId, e.errorCode, e.description)
                }
                .collect {
                    view?.removeReactionSuccess(message.msgId)
                }
        }
    }

    override fun fetchReactionDetail(message: ChatMessage, reaction: String?, pageSize: Int) {
        nextCursor = null
        fetchReactionUserList(message, reaction, "", pageSize, { nextCursor, userList ->
            val result = userList.toMutableList()
            if (message.messageReaction?.filter { it.isAddedBySelf }?.find { it.reaction == reaction } != null) {
                val currentUser = ChatClient.getInstance().currentUser
                val user = if (message.isSingleChat()) {
                    EaseUser(currentUser).getMessageUser(message)
                } else {
                    val groupId = if (message.isChatThreadMessage) {
                        message.getParentId() ?: message.conversationId()
                    } else {
                        message.conversationId()
                    }
                    EaseProfile.getGroupMember(groupId, currentUser)?.toUser() ?: EaseUser(currentUser)
                }
                result.add(0, user)
            }
            view?.fetchReactionDetailSuccess(message.msgId, nextCursor, result)
        }, { errorCode, errorMsg ->
            view?.fetchReactionDetailFail(message.msgId, errorCode, errorMsg)
        })
    }

    override fun fetchMoreReactionDetail(
        message: ChatMessage,
        reaction: String?,
        cursor: String?,
        pageSize: Int
    ) {
        fetchReactionUserList(message, reaction, cursor, pageSize, { nextCursor, userList ->
            this.nextCursor = nextCursor
            view?.fetchMoreReactionDetailSuccess(message.msgId, nextCursor, userList)
        }, { errorCode, errorMsg ->
            view?.fetchMoreReactionDetailFail(message.msgId, errorCode, errorMsg)
        })
    }

    /**
     * Fetch reaction users and exclude current user.
     */
    private fun fetchReactionUserList(
        message: ChatMessage,
        reaction: String?,
        cursor: String?,
        pageSize: Int,
        onSuccess: (nextCursor: String, List<EaseUser>) -> Unit = {_,_ ->},
        onError: (Int, String) -> Unit = {_,_ ->}) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.fetchReactionDetail(message.msgId, reaction, cursor, pageSize))
            }
                .catchChatException { e->
                    onError(e.errorCode, e.description)
                }
                .collect {
                    it.data?.let { data ->
                        val userList = if (data.isEmpty()) {
                            arrayListOf()
                        } else {
                            data[0].userList
                                .filter { userId ->
                                    userId != ChatClient.getInstance().currentUser
                                }.map { user ->
                                    if (message.isSingleChat()) {
                                        EaseUser(user).getMessageUser(message)
                                    } else {
                                        val groupId = if (message.isChatThreadMessage) {
                                            message.getParentId() ?: message.conversationId()
                                        } else {
                                            message.conversationId()
                                        }
                                        EaseProfile.getGroupMember(groupId, user)?.toUser() ?: EaseUser(user)
                                    }
                                }
                        }
                        onSuccess(it.cursor, userList)
                    } ?: run {
                        onError(ChatError.GENERAL_ERROR, "data is null")
                    }
                }
        }
    }

}