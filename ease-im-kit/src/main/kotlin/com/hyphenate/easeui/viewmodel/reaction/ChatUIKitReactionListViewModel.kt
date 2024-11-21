package com.hyphenate.easeui.viewmodel.reaction

import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatType
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IMessageReactionListResultView
import com.hyphenate.easeui.model.ChatUIKitDefaultEmojiIconData
import com.hyphenate.easeui.repository.ChatUIKitManagerRepository
import com.hyphenate.easeui.viewmodel.ChatUIKitBaseViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ChatUIKitReactionListViewModel: ChatUIKitBaseViewModel<IMessageReactionListResultView>(), IMessageReactionListRequest {
    private val chatRepository by lazy { ChatUIKitManagerRepository() }

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
                    if (ChatUIKitDefaultEmojiIconData.mapData.containsKey(reaction.identityCode)) {
                        reaction.icon = ChatUIKitDefaultEmojiIconData.mapData[item.reaction]!!
                    }
                    reaction
                }
                view?.fetchReactionListSuccess(message.msgId, result)
            }
        }
    }

}