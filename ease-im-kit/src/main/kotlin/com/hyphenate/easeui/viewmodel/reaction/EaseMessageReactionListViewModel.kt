package com.hyphenate.easeui.viewmodel.reaction

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReactionListResultView
import com.hyphenate.easeui.model.EaseDefaultEmojiIconData
import com.hyphenate.easeui.repository.EaseChatManagerRepository
import com.hyphenate.easeui.viewmodel.EaseBaseViewModel
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