package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatTextMessageBody
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitLoadDataType
import com.hyphenate.easeui.feature.chat.widgets.ChatUIKitLayout
import com.hyphenate.easeui.viewmodel.messages.IChatViewRequest

class ChatUIKitMessageTranslationController(
    private val context: Context,
    private val chatLayout: ChatUIKitLayout,
    private val viewModel: IChatViewRequest?,
) {
    private var isThread:Boolean = false

    fun showTranslationMessage(message: ChatMessage?){
        ChatUIKitClient.getConfig()?.chatConfig?.targetTranslationLanguage?.let {
            viewModel?.translationMessage(message, mutableListOf(it))
        }
    }

    fun hideTranslationMessage(message: ChatMessage?){
        viewModel?.hideTranslationMessage(message)
    }

    fun setConversationType(isThread: ChatUIKitLoadDataType){
        this.isThread = isThread == ChatUIKitLoadDataType.THREAD
    }

    fun isShowTranslation(message: ChatMessage?):Boolean{
        val containsKey = message?.ext()?.containsKey(ChatUIKitConstant.TRANSLATION_STATUS)
        message?.let {
            if (it.body is ChatTextMessageBody){
                val body = it.body as ChatTextMessageBody
                val translations = body.translations
                containsKey?.let { hasKey->
                    if (hasKey){
                        val isTranslation = it.getBooleanAttribute(ChatUIKitConstant.TRANSLATION_STATUS)
                        isTranslation.let {
                            return it
                        }
                    }
                }
                return translations.size > 0
            }
        }
        return false
    }

}