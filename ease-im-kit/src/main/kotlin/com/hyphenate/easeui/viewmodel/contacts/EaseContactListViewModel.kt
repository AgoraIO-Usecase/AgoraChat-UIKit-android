package com.hyphenate.easeui.viewmodel.contacts

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatContactManager
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.bus.EaseFlowBus
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.collectWithCheckErrorCode
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.common.extensions.toUser
import com.hyphenate.easeui.common.helper.ContactSortedHelper
import com.hyphenate.easeui.feature.contact.interfaces.IEaseContactResultView
import com.hyphenate.easeui.model.EaseEvent
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.model.setUserInitialLetter
import com.hyphenate.easeui.repository.EaseContactListRepository
import com.hyphenate.easeui.repository.EaseConversationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class EaseContactListViewModel(
    val chatContactManager: ChatContactManager = ChatClient.getInstance().contactManager(),
    val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseContactResultView>(),IContactListRequest {

    val repository:EaseContactListRepository = EaseContactListRepository(chatContactManager)
    private val convRepository: EaseConversationRepository = EaseConversationRepository()

    override fun loadData(fetchServerData: Boolean){
        viewModelScope.launch {
            if (fetchServerData) {
                flow {
                    try {
                        emit(repository.loadData())
                    } catch (e:ChatException){
                        emit(Result.failure<ChatException>(e))
                        inMainScope{
                            view?.loadContactListFail(e.errorCode, e.description)
                        }
                    }
                }
                .flatMapConcat {
                    flow {
                        emit(repository.loadLocalContact())
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
                var sortedList = mutableListOf<EaseUser>()
                val data = it
                data?.map {
                    it.setUserInitialLetter()
                }
                data?.let {
                    sortedList = ContactSortedHelper.sortedList(it).toMutableList()
                }
                view?.loadContactListSuccess(sortedList)
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
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), userName)
            .collectWithCheckErrorCode {
                EaseFlowBus.withStick<EaseEvent>(EaseEvent.EVENT.ADD.name)
                    .post(viewModelScope, EaseEvent(EaseEvent.EVENT.ADD.name, EaseEvent.TYPE.CONTACT))
                view?.addContactSuccess(it)
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

    override fun fetchBlockListFromServer(){
        viewModelScope.launch {
            flow {
                emit(repository.getBlockListFromServer())
            }
            .catchChatException { e ->
                view?.fetchBlockListFromServerFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect{
                val data = it
                data?.map {
                    it.setUserInitialLetter()
                }
                data?.let {
                    val sortedList = ContactSortedHelper.sortedList(it)
                    view?.fetchBlockListFromServerSuccess(sortedList.toMutableList())
                }
            }
        }
    }

    override fun getBlockListFromLocal() {
        viewModelScope.launch {
            flow {
                emit(repository.getBlockListFromLocal())
            }.catchChatException { e ->
                view?.getBlockListFromLocalFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect{
                val data = it
                data?.map {
                    it.setUserInitialLetter()
                }
                data?.let {
                    val sortedList = ContactSortedHelper.sortedList(it)
                    view?.getBlockListFromLocalSuccess(sortedList.toMutableList())
                }
            }
        }
    }

    override fun addUserToBlockList(userList:MutableList<String>){
        viewModelScope.launch {
            flow {
                emit(repository.addUserToBlockList(userList))
            }
            .catchChatException { e ->
                view?.addUserToBlockListFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.addUserToBlockListSuccess()
            }
        }
    }

    override fun removeUserFromBlockList(userName: String){
        viewModelScope.launch {
            flow {
                emit(repository.removeUserFromBlockList(userName))
            }
            .catchChatException { e ->
                view?.removeUserFromBlockListFail(e.errorCode,e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.removeUserFromBlockListSuccess()
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

    override fun clearConversationMessage(conversationId: String?) {
        viewModelScope.launch {
            ChatClient.getInstance().chatManager().getConversation(conversationId)?.parse()?.let {
                flow {
                    emit(convRepository.clearConversationMessage(it))
                }
                .catchChatException { e ->
                    view?.clearConversationFail(e.errorCode,e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
                .collectWithCheckErrorCode {
                    view?.clearConversationSuccess(conversationId)
                }
            } ?: view?.clearConversationFail(ChatError.INVALID_PARAM,"conversation is null")
        }
    }

    override fun fetchContactInfo(contactList: List<EaseUser>?) {
        val requestList = contactList?.filter { user ->
            val u = EaseIM.getCache().getUser(user.userId) ?: return@filter true
            u.avatar.isNullOrEmpty() || u.name.isNullOrEmpty()
        }
        viewModelScope.launch {
            flow {
                emit(repository.fetchContactInfo(requestList))
            }
            .catchChatException {  }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    val result = it.map { profile ->
                        ChatLog.d(TAG,"fetchUserInfoByUserSuccess result ${profile.toUser()}")
                        profile.toUser()
                    }
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
                        ChatLog.d(TAG, "makeSilentForContactSuccess")
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
                        ChatLog.d(TAG, "cancelSilentForContactSuccess")
                        view?.cancelSilentForContactSuccess()
                    }
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