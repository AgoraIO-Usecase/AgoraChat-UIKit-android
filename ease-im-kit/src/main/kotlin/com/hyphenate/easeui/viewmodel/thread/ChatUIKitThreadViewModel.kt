package com.hyphenate.easeui.viewmodel.thread

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatGroup
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.isGroupChat
import com.hyphenate.easeui.common.helper.ChatUIKitThreadNotifyHelper
import com.hyphenate.easeui.feature.thread.interfaces.IChatThreadResultView
import com.hyphenate.easeui.repository.ChatUIKitThreadRepository
import com.hyphenate.easeui.repository.ChatUIKitGroupRepository
import com.hyphenate.easeui.viewmodel.ChatUIKitBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ChatUIKitThreadViewModel: ChatUIKitBaseViewModel<IChatThreadResultView>(),IChatThreadRequest {

    private var parentId: String = ""
    private var topicMsgId: String = ""
    private var _conversation: ChatConversation? = null
    private val threadRepository by lazy { ChatUIKitThreadRepository() }
    private val groupRepository by lazy { ChatUIKitGroupRepository() }

    override fun setupWithToConversation(parentId: String, messageId: String) {
        this.parentId = parentId
        this.topicMsgId = messageId

        _conversation = ChatClient.getInstance().chatManager().getConversation(parentId
            ,ChatConversationType.GroupChat , true, true)

    }

    override fun setGroupInfo(parentId: String) {
        if (parentId.isEmpty()) view?.setGroupInfoFail(ChatError.GENERAL_ERROR, "ParentId cannot be an empty string")
        val parent = ChatClient.getInstance().groupManager().getGroup(parentId)
        viewModelScope.launch {
            flow {
                if (parent != null){
                    view?.settGroupInfoSuccess(parent)
                }else{
                    emit(groupRepository.fetchGroupDetails(parentId))
                }
            }
                .catchChatException { e ->
                    view?.setGroupInfoFail(e.errorCode, e.description)
                }
                .collect {
                    view?.settGroupInfoSuccess(it)
                }
        }
    }

    override fun createChatThread(chatThreadName: String, message: ChatMessage) {
        viewModelScope.launch {
            val truncatedStr = if (chatThreadName.length > threadNameMax) {
                chatThreadName.substring(0, threadNameMax)
            } else {
                chatThreadName
            }

            flow {
                emit(threadRepository.createChatThread(parentId, topicMsgId, truncatedStr))
            }
                .catchChatException { e ->
                    view?.onCreateChatThreadFail(e.errorCode, e.description)
                }
                .collect {
                    message.setIsChatThreadMessage(true)
                    message.to = it.chatThreadId
                    ChatClient.getInstance().chatManager().saveMessage(message)
                    view?.onCreateChatThreadSuccess(it, message)
                }
        }
    }

    override fun fetchChatThreadFromServer(chatThreadId: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.fetchChatThreadFromServer(chatThreadId))
            }
                .catchChatException { e ->
                    view?.fetchChatThreadDetailsFromServerFail(e.errorCode, e.description)
                }
                .collect {
                    view?.fetchChatThreadDetailsFromServerSuccess(it)
                }
        }
    }

    override fun fetchChatThreadsFromServer(parentId: String, limit: Int, cursor: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.fetchChatThreadsFromServer(parentId,limit, cursor))
            }
                .catchChatException { e ->
                    view?.fetchChatThreadsFromServerFail(e.errorCode, e.description)
                }
                .collect {
                    view?.fetchChatThreadsFromServerSuccess(it)
                }
        }
    }

    override fun joinChatThread(chatThreadId: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.joinChatThread(chatThreadId))
            }
                .catchChatException { e ->
                    inMainScope {
                        view?.joinChatThreadFail(e.errorCode, e.description)
                    }
                }
                .collect {
                    view?.joinChatThreadSuccess(it)
                }
        }
    }

    override fun destroyChatThread(chatThreadId: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.destroyChatThread(chatThreadId))
            }
                .catchChatException { e ->
                    view?.destroyChatThreadFail(e.errorCode, e.description)
                }
                .collect {
                    _conversation?.conversationId()?.let { conversationId ->
                        ChatUIKitThreadNotifyHelper.removeCreateThreadNotify(
                            conversationId,chatThreadId)
                    }
                    view?.destroyChatThreadSuccess()
                }
        }
    }

    override fun leaveChatThread(chatThreadId: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.leaveChatThread(chatThreadId))
            }
                .catchChatException { e ->
                    view?.leaveChatThreadFail(e.errorCode, e.description)
                }
                .collect {
                    view?.leaveChatThreadSuccess()
                }
        }
    }

    override fun updateChatThreadName(chatThreadId: String, chatThreadName: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.updateChatThreadName(chatThreadId,chatThreadName))
            }
                .catchChatException { e ->
                    view?.updateChatThreadNameFail(e.errorCode, e.description)
                }
                .collect {
                    view?.updateChatThreadNameSuccess()
                }
        }
    }

    override fun getChatThreadMembers(chatThreadId: String, limit: Int, cursor: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.getChatThreadMembers(chatThreadId,limit,cursor))
            }
                .catchChatException { e ->
                    view?.getChatThreadMembersFail(e.errorCode, e.description)
                }
                .collect {
                    view?.getChatThreadMembersSuccess(it)
                }
        }
    }

    override fun removeMemberFromChatThread(chatThreadId: String, member: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.removeMemberFromChatThread(chatThreadId,member))
            }
                .catchChatException { e ->
                    view?.removeMemberFromChatThreadFail(e.errorCode, e.description)
                }
                .collect {
                    view?.removeMemberFromChatThreadSuccess(member)
                }
        }
    }

    override fun getJoinedChatThreadsFromServer(parentId: String?, limit: Int, cursor: String) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.getJoinedChatThreadsFromServer(parentId,limit,cursor))
            }
                .catchChatException { e ->
                    view?.getJoinedChatThreadsFromServerFail(e.errorCode, e.description)
                }
                .collect {
                    view?.getJoinedChatThreadsFromServerSuccess(it)
                }
        }
    }

    override fun getChatThreadLatestMessage(chatThreadIds: List<String>) {
        viewModelScope.launch {
            flow {
                emit(threadRepository.getChatThreadLatestMessage(chatThreadIds))
            }
                .catchChatException { e ->
                    view?.getChatThreadLatestMessageFail(e.errorCode, e.description)
                }
                .collect {
                    view?.getChatThreadLatestMessageSuccess(it)
                }
        }
    }

    override fun checkoutConvScope(){
        if (_conversation == null) {
            inMainScope {
                view?.onThreadErrorBeforeSending(ChatError.INVALID_PARAM, "Conversation is null.")
            }
        }else{
            if (!_conversation!!.isGroupChat) {
                inMainScope {
                    view?.onThreadErrorBeforeSending(ChatError.INVALID_PARAM, "Not group chat.")
                }
            }
        }
    }

    override fun checkoutGroupScope(group: ChatGroup?) {
        if (group == null) {
            inMainScope {
                view?.onThreadErrorBeforeSending(ChatError.INVALID_PARAM, "Group is null.")
            }
        }
    }

    private fun inMainScope(scope: ()->Unit) {
        viewModelScope.launch(context = Dispatchers.Main) {
            scope()
        }
    }

    companion object {
        private val TAG = ChatUIKitThreadViewModel::class.java.simpleName
        private const val threadNameMax = 64
    }
}