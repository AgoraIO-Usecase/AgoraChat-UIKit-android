package com.hyphenate.easeui.viewmodel.conversations

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.viewmodel.ChatUIKitBaseViewModel
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.collectWithCheckErrorCode
import com.hyphenate.easeui.feature.conversation.interfaces.IUIKitConvListResultView
import com.hyphenate.easeui.model.ChatUIKitConversation
import com.hyphenate.easeui.repository.ChatUIKitConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class ChatUIKitConversationListViewModel(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
    private val stopTimeoutMillis: Long = 5000
): ChatUIKitBaseViewModel<IUIKitConvListResultView>(), IConversationListRequest {

    private val repository: ChatUIKitConversationRepository = ChatUIKitConversationRepository(chatManager)

    override fun loadData() {
        viewModelScope.launch {
            flow {
                try {
                    emit(repository.loadData())
                } catch (e:ChatException){
                    emit(Result.failure<ChatException>(e))
                    inMainScope{
                        view?.loadConversationListFail(e.errorCode, e.description)
                    }
                }
            }.flatMapConcat {
                flow {
                    emit(repository.loadLocalConversation())
                }
            }
            .catchChatException { e ->
                view?.loadConversationListFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.loadConversationListSuccess(it)
                }
            }
        }
    }

    override fun sortConversationList(conversations: List<ChatUIKitConversation>) {
        // If you need to sort the conversation list, you can override this method.
        // Then call view?.loadConversationListSuccess(conversations) to update the UI.
    }

    override fun makeConversionRead(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.makeConversionRead(conversation))
            }
            .catchChatException { e ->
                view?.loadConversationListFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.makeConversionReadSuccess(position, conversation)
            }
        }
    }

    override fun makeSilentForConversation(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.makeSilentForConversation(conversation))
            }
            .catchChatException { e ->
                view?.makeSilentForConversationFail(conversation, e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    ChatLog.e("conversation", "makeSilentForConversationSuccess")
                    view?.makeSilentForConversationSuccess(position, conversation)
                }
            }
        }
    }

    override fun cancelSilentForConversation(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.cancelSilentForConversation(conversation))
            }
            .catchChatException { e ->
                view?.cancelSilentForConversationFail(conversation, e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.cancelSilentForConversationSuccess(position, conversation)
            }
        }
    }

    override fun pinConversation(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.pinConversation(conversation))
            }
            .catchChatException { e ->
                view?.pinConversationFail(conversation, e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.pinConversationSuccess(position, conversation)
            }
        }
    }

    override fun unpinConversation(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.unpinConversation(conversation))
            }
            .catchChatException { e ->
                view?.unpinConversationFail(conversation, e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.unpinConversationSuccess(position, conversation)
            }
        }
    }

    override fun deleteConversation(position: Int, conversation: ChatUIKitConversation) {
        viewModelScope.launch {
            flow {
                emit(repository.deleteConversation(conversation))
            }
            .catchChatException { e ->
                view?.deleteConversationFail(conversation, e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.deleteConversationSuccess(position, conversation)
            }
        }
    }

    override fun fetchConvGroupInfo(conversationList: List<ChatUIKitConversation>) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchConvGroupInfo(conversationList))
            }
            .catchChatException {  }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                it?.forEach { item ->
                    ChatUIKitClient.getCache().insertGroup(item.id, item)
                }
                view?.fetchConversationInfoByUserSuccess(it)
            }
        }
    }

    override fun fetchConvUserInfo(conversationList: List<ChatUIKitConversation>) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchConvUserInfo(conversationList))
            }
                .catchChatException {  }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    it?.forEach { item ->
                        ChatUIKitClient.getCache().insertUser(item)
                    }
                    view?.fetchConversationInfoByUserSuccess(it)
                }
        }
    }

    private fun inMainScope(scope: ()->Unit) {
        viewModelScope.launch(context = Dispatchers.Main) {
            scope()
        }
    }

}