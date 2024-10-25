package io.agora.uikit.viewmodel.reaction

import androidx.lifecycle.viewModelScope
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatType
import io.agora.uikit.common.extensions.catchChatException
import io.agora.uikit.common.extensions.parse
import io.agora.uikit.feature.chat.reaction.interfaces.IMessageReactionListResultView
import io.agora.uikit.model.EaseDefaultEmojiIconData
import io.agora.uikit.repository.EaseChatManagerRepository
import io.agora.uikit.viewmodel.EaseBaseViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class EaseMessageReactionListViewModel: EaseBaseViewModel<IMessageReactionListResultView>(), IMessageReactionListRequest {
    private val chatRepository by lazy { EaseChatManagerRepository() }

    override fun fetchReactionList(message: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.fetchReactionList(arrayListOf(message.msgId)
                    , message.chatType
                    , if (message.chatType == ChatType.GroupChat) message.conversationId() else null))
            }
            .catchChatException { e->
                view?.fetchReactionListFail(message.msgId, e.errorCode, e.description)
            }
            .collect {
                val result = it[message.msgId]?.map { item ->
                    val reaction = item.parse()
                    if (EaseDefaultEmojiIconData.mapData.containsKey(reaction.identityCode)) {
                        reaction.icon = EaseDefaultEmojiIconData.mapData[item.reaction]!!
                    }
                    reaction
                }
                view?.fetchReactionListSuccess(message.msgId, result)
            }
        }
    }

}