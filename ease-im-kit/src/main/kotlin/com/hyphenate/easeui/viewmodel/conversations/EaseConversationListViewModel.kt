package com.hyphenate.easeui.viewmodel.conversations

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.ChatManager
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.collectWithCheckErrorCode
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.feature.conversation.interfaces.IEaseConvListResultView
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.repository.EaseConversationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class EaseConversationListViewModel(
    private val chatManager: ChatManager = ChatClient.getInstance().chatManager(),
    private val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseConvListResultView>(), IConversationListRequest {

    private val repository: EaseConversationRepository = EaseConversationRepository(chatManager)

    override fun loadData() {
        viewModelScope.launch {
            flow {
                emit(repository.loadData())
            }
            .catchChatException { e ->
                view?.loadConversationListFail(e.errorCode, e.description)
                val localData = chatManager.allConversationsBySort?.filter {
                    it.conversationId() != EaseConstant.DEFAULT_SYSTEM_MESSAGE_ID && it.allMessages.isNotEmpty()
                }?.map { it.parse() } ?: listOf()
                view?.loadLocalConversationListFinished(localData)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.loadConversationListSuccess(it)
                }
            }
        }
    }

    override fun sortConversationList(conversations: List<EaseConversation>) {
        // If you need to sort the conversation list, you can override this method.
        // Then call view?.loadConversationListSuccess(conversations) to update the UI.
    }

    override fun makeConversionRead(position: Int, conversation: EaseConversation) {
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

    override fun makeSilentForConversation(position: Int, conversation: EaseConversation) {
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

    override fun cancelSilentForConversation(position: Int, conversation: EaseConversation) {
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

    override fun pinConversation(position: Int, conversation: EaseConversation) {
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

    override fun unpinConversation(position: Int, conversation: EaseConversation) {
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

    override fun deleteConversation(position: Int, conversation: EaseConversation) {
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

    override fun fetchConvGroupInfo(conversationList: List<EaseConversation>) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchConvGroupInfo(conversationList))
            }
            .catchChatException {  }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    it?.forEach { item ->
                        EaseIM.getCache().insertGroup(item.id, item)
                    }
                    view?.fetchConversationInfoByUserSuccess(it)
                }
            }
        }
    }

    override fun fetchConvUserInfo(conversationList: List<EaseConversation>) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchConvUserInfo(conversationList))
            }
                .catchChatException {  }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        it?.forEach { item ->
                            EaseIM.getCache().insertUser(item)
                        }
                        view?.fetchConversationInfoByUserSuccess(it)
                    }
                }
        }
    }

}