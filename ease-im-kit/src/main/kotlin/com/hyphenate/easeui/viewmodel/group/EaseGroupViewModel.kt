package com.hyphenate.easeui.viewmodel.group

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatGroupManager
import com.hyphenate.easeui.common.ChatGroupOptions
import com.hyphenate.easeui.common.ChatLog
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.collectWithCheckErrorCode
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.common.helper.ContactSortedHelper
import com.hyphenate.easeui.feature.group.interfaces.IEaseGroupResultView
import com.hyphenate.easeui.model.setUserInitialLetter
import com.hyphenate.easeui.repository.EaseChatThreadRepository
import com.hyphenate.easeui.repository.EaseConversationRepository
import com.hyphenate.easeui.repository.EaseGroupRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class EaseGroupViewModel(
    private val groupManager: ChatGroupManager = ChatClient.getInstance().groupManager(),
    private val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseGroupResultView>(),IGroupRequest{

    companion object{
        const val pageSize:Int = 20
        const val needMemberCount:Boolean = false
        const val needRole:Boolean = false
    }

    private val repository:EaseGroupRepository = EaseGroupRepository(groupManager)
    private val convRepository: EaseConversationRepository = EaseConversationRepository()
    private val threadRepository by lazy { EaseChatThreadRepository() }

    override fun loadJoinedGroupData(page:Int) {
        viewModelScope.launch {
            flow {
                emit(repository.loadJoinedGroupData(page,pageSize,needMemberCount,needRole))
            }
            .catchChatException { e ->
                view?.loadGroupListFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.loadGroupListSuccess(it.toMutableList())
                }
            }
        }
    }

    override fun loadLocalJoinedGroupData() {
        viewModelScope.launch {
            flow {
                emit(repository.loadLocalJoinedGroupData())
            }
                .catchChatException { e ->
                    view?.loadLocalGroupListFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        view?.loadLocalGroupListSuccess(it.toMutableList())
                    }
                }
        }
    }

    override fun createGroup(
        groupName: String,
        desc: String,
        members: MutableList<String>,
        reason: String,
        options: ChatGroupOptions
    ) {
        viewModelScope.launch {
            flow {
                emit(repository.createGroup(groupName, desc, members, reason, options))
            }
            .catchChatException { e ->
                view?.createGroupFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                it?.let { it1 -> view?.createGroupSuccess(it1) }
            }
        }
    }

    override fun fetchGroupMemberFromService(groupId: String) {
        viewModelScope.launch {
            flow {
                emit(repository.fetGroupMemberFromService(groupId))
            }
            .catchChatException { e ->
                view?.fetchGroupMemberFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), mutableListOf())
            .collect {
                val data = it.map { user->
                    user.setUserInitialLetter()
                    user
                }
                val sortedList = ContactSortedHelper.sortedList(data)
                view?.fetchGroupMemberSuccess(sortedList)
            }
        }
    }

    override fun loadLocalMember(groupId: String) {
        viewModelScope.launch {
            flow {
                emit(repository.loadLocalMember(groupId))
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), mutableListOf())
            .collect {
                it.let { it1 -> view?.loadLocalMemberSuccess(it1) }
            }
        }
    }

    override fun fetchGroupDetails(groupId: String) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchGroupDetails(groupId))
            }
            .catchChatException { e ->
                view?.fetchGroupDetailFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                it?.let { it1 -> view?.fetchGroupDetailSuccess(it1) }
            }
        }
    }

    override fun addGroupMember(groupId: String, members: MutableList<String>) {
        viewModelScope.launch {
            flow {
                emit(repository.addGroupMember(groupId,members))
            }
            .catchChatException { e ->
                view?.addGroupMemberFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.addGroupMemberSuccess()
            }
        }
    }

    override fun removeGroupMember(groupId: String, members: MutableList<String>) {
        viewModelScope.launch {
            flow {
                emit(repository.removeGroupMember(groupId,members))
            }
            .catchChatException { e ->
                view?.removeChatGroupMemberFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.removeChatGroupMemberSuccess()
            }
        }
    }

    override fun leaveChatGroup(groupId: String) {
        viewModelScope.launch {
            flow {
                emit(repository.leaveChatGroup(groupId))
            }
            .catchChatException { e ->
                view?.leaveChatGroupFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.leaveChatGroupSuccess()
            }
        }
    }

    override fun destroyChatGroup(groupId: String) {
        viewModelScope.launch {
            flow {
                emit(repository.destroyChatGroup(groupId))
            }
            .catchChatException { e ->
                view?.destroyChatGroupFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.destroyChatGroupSuccess()
            }
        }
    }

    override fun changeChatGroupName(groupId: String, newName: String) {
        viewModelScope.launch {
            flow {
                emit(repository.changeChatGroupName(groupId, newName))
            }
            .catchChatException { e ->
                view?.changeChatGroupNameFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.changeChatGroupNameSuccess()
            }
        }
    }

    override fun changeChatGroupDescription(groupId: String, description: String) {
        viewModelScope.launch {
            flow {
                emit(repository.changeChatGroupDescription(groupId, description))
            }
            .catchChatException { e ->
                view?.changeChatGroupDescriptionFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.changeChatGroupDescriptionSuccess()
            }
        }
    }

    override fun changeChatGroupOwner(groupId: String, newOwner: String) {
        viewModelScope.launch {
            flow {
                emit(repository.changeChatGroupOwner(groupId, newOwner))
            }
            .catchChatException { e ->
                view?.changeChatGroupOwnerFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.EM_NO_ERROR)
            .collectWithCheckErrorCode {
                view?.changeChatGroupOwnerSuccess()
            }
        }
    }

    override fun fetchGroupMemberAllAttributes(
        groupId: String,
        userList: List<String>,
        keyList: List<String>,
    ) {
        viewModelScope.launch {
            flow {
                emit(repository.fetchMemberAllAttributes(groupId,userList,keyList))
            }
            .catchChatException { e ->
                view?.getGroupMemberAllAttributesFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.getGroupMemberAllAttributesSuccess(userList, it)
                }
            }
        }
    }

    override fun setGroupMemberAttributes(groupId: String, userId: String,attribute:MutableMap<String,String>) {
        viewModelScope.launch {
            flow {
                emit(repository.setGroupMemberAttributes(groupId, userId,attribute))
            }
            .catchChatException { e ->
                view?.setGroupMemberAttributesFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR
            ).collectWithCheckErrorCode {
                view?.setGroupMemberAttributesSuccess()
            }
        }
    }

    override fun fetchMemberInfo(groupId: String?, members: List<String>?) {
        val filterList = members?.filter {
            EaseIM.getCache().getUser(it) == null
        }
        if (filterList.isNullOrEmpty()) {
            return
        }
        viewModelScope.launch {
            flow {
                emit(repository.fetchMemberInfo(groupId, members))
            }
            .catchChatException { e ->
                view?.fetchMemberInfoFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null
            ).collect {
                if (it != null) {
                    val map = it.associateBy { profile->
                        EaseIM.getCache().insertUser(profile)
                        profile.id
                    }
                    view?.fetchMemberInfoSuccess(map)
                }
            }
        }
    }


    override fun deleteConversation(conversationId: String?) {
        viewModelScope.launch {
            ChatClient.getInstance().chatManager().getConversation(conversationId,ChatConversationType.GroupChat)?.parse()?.let {
                flow {
                    emit(convRepository.deleteConversation(it))
                }
                    .catchChatException { e ->
                        view?.deleteConversationByGroupFail(e.errorCode,e.description)
                    }
                    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
                    .collectWithCheckErrorCode {
                        view?.deleteConversationByGroupSuccess(conversationId)
                    }
            } ?: view?.deleteConversationByGroupFail(ChatError.INVALID_PARAM,"conversation is null")
        }
    }

    override fun makeSilentModeForConversation(
        conversationId: String,
        conversationType:ChatConversationType
    ) {
        viewModelScope.launch {
            flow {
                emit(convRepository.makeSilentForConversation(conversationId,conversationType))
            }
                .catchChatException { e ->
                    view?.makeSilentForGroupFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        ChatLog.e("conversation", "makeSilentForGroupSuccess")
                        view?.makeSilentForGroupSuccess(it)
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
                    view?.cancelSilentForGroupFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        ChatLog.e("conversation", "cancelSilentForGroupSuccess")
                        view?.cancelSilentForGroupSuccess()
                    }
                }
        }
    }


    override fun updateChatThreadName(chatThreadId: String, chatThreadName: String){
        viewModelScope.launch {
            flow {
                emit(threadRepository.updateChatThreadName(chatThreadId,chatThreadName))
            }
                .catchChatException { e ->
                    view?.changeThreadNameFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.EM_NO_ERROR)
                .collect {
                    view?.changeThreadNameSuccess()
                }
        }
    }


}