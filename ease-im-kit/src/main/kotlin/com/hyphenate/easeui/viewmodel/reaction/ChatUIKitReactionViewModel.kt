package com.hyphenate.easeui.viewmodel.reaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageReaction
import com.hyphenate.easeui.common.extensions.catchChatException
import com.hyphenate.easeui.common.extensions.parse
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitReactionType
import com.hyphenate.easeui.feature.chat.reaction.interfaces.IChatReactionResultView
import com.hyphenate.easeui.model.ChatUIKitDefaultEmojiIconData
import com.hyphenate.easeui.model.ChatUIKitReaction
import com.hyphenate.easeui.model.toReaction
import com.hyphenate.easeui.repository.ChatUIKitManagerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ChatUIKitReactionViewModel: ViewModel(), IChatReactionRequest {
    private val chatRepository by lazy { ChatUIKitManagerRepository() }

    private val viewMap: MutableMap<String, IChatReactionResultView> = mutableMapOf()
    private val dialogMap: MutableMap<String, IChatReactionResultView> = mutableMapOf()

    override fun attachView(message: ChatMessage, view: IChatReactionResultView, isDialog: Boolean) {
        if (isDialog) {
            dialogMap[message.msgId] = view
        } else {
            viewMap[message.msgId] = view
        }
    }

    override fun detachView(message: ChatMessage, isMenuDialog: Boolean) {
        if (isMenuDialog) {
            dialogMap.remove(message.msgId)
        } else {
            viewMap.remove(message.msgId)
        }
    }

    private fun getTargetView(messageId: String): IChatReactionResultView? {
        return viewMap[messageId]
    }

    private fun getTargetDialogView(messageId: String): IChatReactionResultView? {
        return dialogMap[messageId]
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDefaultReactions(message: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(message.messageReaction?.filter { it.isAddedBySelf }?.associateBy { it.reaction }
                    ?: mapOf<String, ChatMessageReaction>())
            }
            .flatMapConcat { value ->
                flow {
                    emit(ChatUIKitDefaultEmojiIconData.defaultReactions.map {
                        it.isAddedBySelf = value.containsKey(it.identityCode)
                        it
                    })
                }
            }
            .collect {
                val defaultReactions = it.toMutableList()
                val enableWxStyle = ChatUIKitClient.getConfig()?.chatConfig?.enableWxMessageStyle
                if (enableWxStyle == true){
                    defaultReactions.remove(defaultReactions[defaultReactions.size - 1])
                }
                defaultReactions.run {
                    add(ChatUIKitReaction(icon = R.drawable.emoji_more, type = ChatUIKitReactionType.ADD))
                    getTargetView(message.msgId)?.getDefaultReactionsSuccess(this)
                    getTargetDialogView(message.msgId)?.getDefaultReactionsSuccess(this)
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getAllChatReactions(message: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(message.messageReaction?.filter { it.isAddedBySelf }?.associateBy { it.reaction }
                    ?: mapOf<String, ChatMessageReaction>())
            }
                .flatMapConcat { value ->
                    flow {
                        emit(ChatUIKitDefaultEmojiIconData.data.filterNotNull().map {
                            it.toReaction().apply {
                                isAddedBySelf = value.containsKey(identityCode)
                            }
                        })
                    }
                }
                .collect {
                    it.toMutableList().run {
                        getTargetDialogView(message.msgId)?.getAllChatReactionsSuccess(this)
                    }
                }
        }
    }

    override fun getMessageReactions(message: ChatMessage) {
        viewModelScope.launch {
            flow {
                emit(
                    message.messageReaction?.map {
                        val reaction = it.parse()
                        if (ChatUIKitDefaultEmojiIconData.mapData.containsKey(it.reaction)) {
                            reaction.icon = ChatUIKitDefaultEmojiIconData.mapData[it.reaction]!!
                        }
                        reaction.type = ChatUIKitReactionType.NORMAL
                        reaction
                    } ?: listOf()
                )
            }
            .collect {
                getTargetView(message.msgId)?.getMessageReactionSuccess(it.toMutableList())
                getTargetDialogView(message.msgId)?.getMessageReactionSuccess(it.toMutableList())
            }
        }
    }

    override fun addReaction(message: ChatMessage, reaction: String) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.addReaction(message.msgId, reaction))
            }
            .catchChatException { e ->
                getTargetView(message.msgId)?.addReactionFail(message.msgId, e.errorCode, e.description)
                getTargetDialogView(message.msgId)?.addReactionFail(message.msgId, e.errorCode, e.description)
            }
            .collect {
                getTargetView(message.msgId)?.addReactionSuccess(message.msgId)
                getTargetDialogView(message.msgId)?.addReactionSuccess(message.msgId)
            }
        }
    }

    override fun removeReaction(message: ChatMessage, reaction: String) {
        viewModelScope.launch {
            flow {
                emit(chatRepository.removeReaction(message.msgId, reaction))
            }
            .catchChatException { e->
                getTargetView(message.msgId)?.removeReactionFail(message.msgId, e.errorCode, e.description)
                getTargetDialogView(message.msgId)?.removeReactionFail(message.msgId, e.errorCode, e.description)
            }
            .collect {
                getTargetView(message.msgId)?.removeReactionSuccess(message.msgId)
                getTargetDialogView(message.msgId)?.removeReactionSuccess(message.msgId)
            }
        }
    }

}