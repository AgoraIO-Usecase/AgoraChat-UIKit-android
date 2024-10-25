package io.agora.uikit.viewmodel.request

import android.content.Context
import androidx.lifecycle.viewModelScope
import io.agora.uikit.EaseIM
import io.agora.uikit.viewmodel.EaseBaseViewModel
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatLog
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.catchChatException
import io.agora.uikit.common.extensions.collectWithCheckErrorCode
import io.agora.uikit.feature.invitation.interfaces.IEaseNotificationResultView
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.repository.EaseNotificationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EaseNotificationViewModel (
    private val stopTimeoutMillis: Long = 5000
): EaseBaseViewModel<IEaseNotificationResultView>(),INotificationRequest{

    private val repository: EaseNotificationRepository = EaseNotificationRepository()

    override fun loadMoreMessage(startMsgId:String?,limit:Int) {
        viewModelScope.launch {
            flow {
                emit(repository.loadMoreMessage(startMsgId,limit))
            }
            .catchChatException { e ->
                view?.loadMoreMessageFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
            .collect {
                if (it != null) {
                    view?.loadMoreMessageSuccess(it)
                }
            }
        }
    }

    override fun loadLocalData() {
        viewModelScope.launch {
            flow {
                emit(repository.getAllMessage())
            }
                .catchChatException { e ->
                    view?.getLocalMessageFail(e.errorCode, e.description)
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null)
                .collect {
                    if (it != null) {
                        view?.getLocalMessageSuccess(it)
                    }
                }
        }
    }

    override fun fetchProfileInfo(members: List<String>?) {
        // 有本地缓存的ids列表
        val filterHasDataList = members?.filter {
            EaseIM.getUserProvider()?.getUser(it) != null
        }?.map { id->
            EaseIM.getUserProvider()?.getUser(id)?:EaseProfile(id)
        }

        // 没有本地缓存的ids列表
        val filterHasNoDataList = members?.filter {
            EaseIM.getCache().getUser(it) == null
        }
        if (filterHasDataList.isNullOrEmpty() && filterHasNoDataList.isNullOrEmpty()) return

        // 如果需要从服务端请求的ids列表为空 则返回本地有缓存数据的ids列表
        if (filterHasNoDataList.isNullOrEmpty()){
            filterHasDataList?.let { hasDataList ->
                if (hasDataList.isNotEmpty()){
                    val hasDataMap = hasDataList.associateBy { profile->
                        profile.id
                    }
                    if (hasDataMap.isNotEmpty()){
                        view?.fetchProfileSuccess(hasDataMap)
                    }
                    return
                }
            }
        }

        viewModelScope.launch {
            flow {
                emit(repository.fetchProfileInfo(filterHasNoDataList))
            }
            .catchChatException { e ->
                view?.fetchProfileFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), null
            ).collect {
                if (it != null) {
                    val map = it.associateBy { profile->
                        EaseIM.getCache().insertUser(profile)
                        profile.id
                    }
                    filterHasDataList?.let { hasDataList ->
                        if (hasDataList.isNotEmpty()){
                            val hasDataMap = hasDataList.associateBy { profile->
                                profile.id
                            }
                            map.toMutableMap().putAll(hasDataMap)
                        }
                    }
                    view?.fetchProfileSuccess(map)
                }
            }
        }
    }

    override fun agreeInvite(context: Context,msg: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(repository.agreeInvite(context, msg))
            }
            .catchChatException { e ->
                view?.agreeInviteFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                var userId = ""
                if (msg.ext().containsKey(EaseConstant.SYSTEM_MESSAGE_FROM)){
                    userId = msg.getStringAttribute(EaseConstant.SYSTEM_MESSAGE_FROM,"")
                }
                view?.agreeInviteSuccess(userId,msg)
            }
        }
    }

    override fun refuseInvite(context: Context,msg: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(repository.refuseInvite(context, msg))
            }
            .catchChatException { e ->
                view?.refuseInviteFail(e.errorCode, e.description)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), ChatError.GENERAL_ERROR)
            .collectWithCheckErrorCode {
                view?.refuseInviteSuccess()
            }
        }
    }

    override fun removeInviteMsg(msg: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(repository.removeInviteMsg(msg))
            }
            .catchChatException { e ->
                ChatLog.e("EaseNotificationViewModel","removeInviteMsg error")
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis), true)
            .collectWithCheckErrorCode {
                ChatLog.d("EaseNotificationViewModel","removeInviteMsg suc $it")
            }
        }
    }

}