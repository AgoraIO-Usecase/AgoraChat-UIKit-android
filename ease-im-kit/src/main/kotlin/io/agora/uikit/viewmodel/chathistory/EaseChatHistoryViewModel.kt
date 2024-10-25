package io.agora.uikit.viewmodel.chathistory

import androidx.lifecycle.viewModelScope
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatValueCallback
import io.agora.uikit.feature.chat.interfaces.IChatHistoryResultView
import io.agora.uikit.repository.EaseChatManagerRepository
import io.agora.uikit.viewmodel.EaseBaseViewModel
import kotlinx.coroutines.launch

class EaseChatHistoryViewModel: EaseBaseViewModel<IChatHistoryResultView>(), IChatHistoryRequest {

    private val chatRepository by lazy { EaseChatManagerRepository() }

    override fun downloadCombineMessage(message: ChatMessage?) {
        viewModelScope.launch {
            chatRepository.downloadCombinedMessageAttachment(message, object : ChatValueCallback<List<ChatMessage>> {
                override fun onSuccess(value: List<ChatMessage>?) {
                    if (value.isNullOrEmpty()) {
                        view?.downloadCombinedMessagesFail(ChatError.GENERAL_ERROR, "No message found.")
                        return
                    }
                    view?.downloadCombinedMessagesSuccess(value)
                }

                override fun onError(error: Int, errorMsg: String?) {
                    view?.downloadCombinedMessagesFail(error, errorMsg)
                }
            })
        }
    }
}