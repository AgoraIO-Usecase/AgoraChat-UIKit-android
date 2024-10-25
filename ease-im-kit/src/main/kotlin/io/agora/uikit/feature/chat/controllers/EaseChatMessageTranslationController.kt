package io.agora.uikit.feature.chat.controllers

import android.content.Context
import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatTextMessageBody
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.feature.chat.enums.EaseLoadDataType
import io.agora.uikit.feature.chat.widgets.EaseChatLayout
import io.agora.uikit.viewmodel.messages.IChatViewRequest

class EaseChatMessageTranslationController(
    private val context: Context,
    private val chatLayout: EaseChatLayout,
    private val viewModel: IChatViewRequest?,
) {
    private var isThread:Boolean = false

    fun showTranslationMessage(message: ChatMessage?){
        EaseIM.getConfig()?.chatConfig?.targetTranslationLanguage?.let {
            viewModel?.translationMessage(message, mutableListOf(it))
        }
    }

    fun hideTranslationMessage(message: ChatMessage?){
        viewModel?.hideTranslationMessage(message)
    }

    fun setConversationType(isThread: EaseLoadDataType){
        this.isThread = isThread == EaseLoadDataType.THREAD
    }

    fun isShowTranslation(message: ChatMessage?):Boolean{
        val containsKey = message?.ext()?.containsKey(EaseConstant.TRANSLATION_STATUS)
        message?.let {
            if (it.body is ChatTextMessageBody){
                val body = it.body as ChatTextMessageBody
                val translations = body.translations
                containsKey?.let { hasKey->
                    if (hasKey){
                        val isTranslation = it.getBooleanAttribute(EaseConstant.TRANSLATION_STATUS)
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