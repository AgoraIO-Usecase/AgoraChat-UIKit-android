package com.hyphenate.easeui.viewmodel.contacts

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatContactManager
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.collectWithCheckErrorCode
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.common.helper.ContactSortedHelper
import com.hyphenate.easeui.common.helper.EasePreferenceManager
import com.hyphenate.easeui.feature.contact.interfaces.IEaseContactResultView
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.setUserInitialLetter
import com.hyphenate.easeui.repository.EaseContactListRepository
import com.hyphenate.easeui.repository.EaseConversationRepository
import com.hyphenate.easeui.repository.EasePresenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class EaseContactListViewModel(
    private val chatContactManager: ChatContactManager = ChatClient.getInstance().contactManager(),
    val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseContactResultView>(),IContactListRequest {

    val repository:EaseContactListRepository = EaseContactListRepository(chatContactManager)
    private val convRepository: EaseConversationRepository = EaseConversationRepository()
    private val presenceRepository by lazy { EasePresenceRepository() }

    override fun loadData(fetchServerData: Boolean){
        viewModelScope.launch {
            if (fetchServerData || !EasePreferenceManager.getInstance().isLoadedContactFromServer()) {
                flow {
                    emit(repository.loadLocalContact())
                }
                .flatMapConcat {
                    flow {
                        emit(repository.loadData())
                    }
                }
            } else {
                flow {
                    emit(repository.loadLocalContact())
                }
            }
            .catchChatException { e ->
                view?.loadContactListFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                val data = it
                data?.map {
                    it.setUserInitialLetter()
                }
                data?.let {
                    val sortedList = ContactSortedHelper.sortedList(it)
                    view?.loadContactListSuccess(sortedList.toMutableList())
                }
            }
        }
    }

    override fun addContact(userName: String, reason: String?){
        viewModelScope.launch {
            flow {
                emit(repository.addContact(userName, reason))
            }
            .catchChatException { e ->
                view?.addContactFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.addContactSuccess()
            }
        }
    }

    override fun deleteContact(userName: String, keepConversation: Boolean?){
        viewModelScope.launch {
            flow {
                emit(repository.deleteContact(userName, keepConversation))
            }
            .catchChatException { e ->
                view?.deleteContactFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.deleteContactSuccess()
            }
        }
    }

    override fun getBlackListFromServer(){
        viewModelScope.launch {
            flow {
                emit(repository.getBlackListFromServer())
            }
            .catchChatException { e ->
                view?.getBlackListFromServerFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect{
                if (it != null) {
                    view?.getBlackListFromServerSuccess(it)
                }
            }
        }
    }

    override fun addUserToBlackList(userList:MutableList<String>){
        viewModelScope.launch {
            flow {
                emit(repository.addUserToBlackList(userList))
            }
            .catchChatException { e ->
                view?.addUserToBlackListFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.addUserToBlackListSuccess()
            }
        }
    }

    override fun removeUserFromBlackList(userName: String){
        viewModelScope.launch {
            flow {
                emit(repository.removeUserFromBlackList(userName))
            }
            .catchChatException { e ->
                view?.removeUserFromBlackListFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.removeUserFromBlackListSuccess()
            }
        }
    }

    override fun acceptInvitation(userName: String) {
        viewModelScope.launch {
            flow {
                emit(repository.acceptInvitation(userName))
            }
            .catchChatException { e ->
                view?.acceptInvitationFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.acceptInvitationSuccess()
            }
        }
    }

    override fun declineInvitation(userName: String) {
        viewModelScope.launch {
            flow {
                emit(repository.declineInvitation(userName))
            }
            .catchChatException { e ->
                view?.declineInvitationFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.declineInvitationSuccess()
            }
        }
    }

    override fun deleteConversation(conversationId: String?) {
        viewModelScope.launch {
            ChatClient.getInstance().chatManager().getConversation(conversationId)?.parse()?.let {
                flow {
                    emit(convRepository.deleteConversation(it))
                }
                .catchChatException { e ->
                    view?.deleteConversationFail(e.errorCode,e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
                .collectWithCheckErrorCode {
                    view?.deleteConversationSuccess(conversationId)
                }
            } ?: view?.deleteConversationFail(ChatError.INVALID_PARAM,"conversation is null")
        }
    }

    override fun fetchContactInfo(contactList: List<EaseUser>) {
        val requestList = contactList.filter { EaseIM.getCache().getUser(it.userId) == null }
        if (requestList.isEmpty()) {
            return
        }
        viewModelScope.launch {
            flow {
                emit(repository.fetchContactInfo(requestList))
            }
            .catchChatException {  }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    val result = it.map { it.toUser() }
                    view?.fetchUserInfoByUserSuccess(result)
                }
            }
        }
    }

    override fun makeSilentModeForConversation(
        conversationId: String,
        conversationType:ChatConversationType,
    ) {
        viewModelScope.launch {
            flow {
                emit(convRepository.makeSilentForConversation(conversationId,conversationType))
            }
                .catchChatException { e ->
                    view?.makeSilentForContactFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        ChatLog.e("conversation", "makeSilentForContactSuccess")
                        view?.makeSilentForContactSuccess(it)
                    }
                }
        }

    }

    override fun cancelSilentForConversation(
        conversationId: String,
        conversationType: ChatConversationType
    ) {
        viewModelScope.launch {
            flow {
                emit(convRepository.cancelSilentForConversation(conversationId,conversationType))
            }
                .catchChatException { e ->
                    view?.cancelSilentForContactFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        ChatLog.e("conversation", "cancelSilentForContactSuccess")
                        view?.cancelSilentForContactSuccess()
                    }
                }
        }
    }

    override fun fetchChatPresence(userIds: MutableList<String>) {
        viewModelScope.launch {
            flow {
                emit(presenceRepository.fetchPresenceStatus(userIds))
            }
                .catchChatException { e->
                    view?.fetchChatPresenceFail(e.errorCode, e.description)
                }
                .collect {
                    view?.fetchChatPresenceSuccess(it)
                }
        }
    }

    private fun inMainScope(scope: ()->Unit) {
        viewModelScope.launch(context = Dispatchers.Main) {
            scope()
        }
    }

    companion object {
        private val TAG = EaseContactListViewModel::class.java.simpleName
    }

}