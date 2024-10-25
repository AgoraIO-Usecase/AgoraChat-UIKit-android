package io.agora.uikit.viewmodel.conversations

import androidx.lifecycle.viewModelScope
import io.agora.uikit.EaseIM
import io.agora.uikit.viewmodel.EaseBaseViewModel
import io.agora.uikit.common.ChatClient
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatManager
import io.agora.uikit.common.extensions.catchChatException
import io.agora.uikit.common.extensions.collectWithCheckErrorCode
import io.agora.uikit.feature.conversation.interfaces.IEaseConvListResultView
import io.agora.uikit.model.EaseConversation
import io.agora.uikit.repository.EaseConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
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
                it?.forEach { item ->
                    EaseIM.getCache().insertGroup(item.id, item)
                }
                view?.fetchConversationInfoByUserSuccess(it)
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
                    it?.forEach { item ->
                        EaseIM.getCache().insertUser(item)
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