package com.hyphenate.easeui.viewmodel.presence

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.feature.chat.interfaces.IPresenceResultView
import com.hyphenate.easeui.repository.EasePresenceRepository
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class EasePresenceViewModel: EaseBaseViewModel<IPresenceResultView>(), IPresenceRequest {

    private val presenceRepository by lazy { EasePresenceRepository() }

    override fun publishPresence(ext: String?) {
        viewModelScope.launch {
            checkEnablePresence{
                flow {
                    ext?.let {
                        emit(presenceRepository.publishPresence(it))
                    }
                }
                    .catchChatException { e ->
                        view?.onPublishPresenceFail(e.errorCode, e.description)
                    }
                    .collect {
                        view?.onPublishPresenceSuccess()
                    }
            }
        }
    }

    override fun fetchPresenceStatus(userIds: MutableList<String>?) {
        viewModelScope.launch {
            checkEnablePresence{
                flow {
                    userIds?.let {
                        emit(presenceRepository.fetchPresenceStatus(it))
                    }
                }
                    .catchChatException { e ->
                        view?.fetchPresenceStatusFail(e.errorCode, e.description)
                    }
                    .collect {
                        view?.fetchPresenceStatusSuccess(it)
                    }
            }
        }
    }

    override fun subscribePresences(userIds: MutableList<String>?, expiry: Long) {
        viewModelScope.launch {
            checkEnablePresence{
                flow {
                    userIds?.let {
                        emit(presenceRepository.subscribePresences(it,expiry))
                    }
                }
                    .catchChatException { e ->
                        view?.subscribePresenceFail(e.errorCode, e.description)
                    }
                    .collect {
                        view?.subscribePresenceSuccess(it)
                    }
            }
        }
    }

    override fun unsubscribePresences(userIds: MutableList<String>?) {
        viewModelScope.launch {
            checkEnablePresence{
                flow {
                    userIds?.let {
                        emit(presenceRepository.unSubscribePresences(it))
                    }
                }
                    .catchChatException { e ->
                        view?.unSubscribePresenceFail(e.errorCode, e.description)
                    }
                    .collect {
                        view?.unSubscribePresenceSuccess()
                    }
            }
        }
    }


    private inline fun checkEnablePresence(scope: ()->Unit) {
        EaseIM.getConfig()?.presencesConfig?.enablePresences?.let {
            if (!it) {
                return
            }
        }
        scope()
    }


}