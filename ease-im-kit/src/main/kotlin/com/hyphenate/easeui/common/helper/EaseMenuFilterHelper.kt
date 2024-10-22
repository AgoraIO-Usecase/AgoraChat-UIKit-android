package com.hyphenate.easeui.common.helper

import com.hyphenate.easeui.R
import com.hyphenate.easeui.common.ChatCustomMessageBody
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageType
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.menu.chat.EaseChatMenuHelper

object EaseMenuFilterHelper {
    fun filterMenu(helper: EaseChatMenuHelper?, message: ChatMessage?){
        message?.let {
            when(it.type){
                ChatMessageType.TXT -> {
                    val isThreadNotify: Boolean =
                        it.getBooleanAttribute(EaseConstant.THREAD_NOTIFICATION_TYPE, false)
                    val isRecallMessage: Boolean =
                        message.getBooleanAttribute(EaseConstant.MESSAGE_TYPE_RECALL, false)
                    val isPinMessage: Boolean =
                        message.getBooleanAttribute(EaseConstant.MESSAGE_PIN_NOTIFY, false)
                    if (isThreadNotify || isRecallMessage || isPinMessage){
                        defaultFilterRow(helper)
                    }
                }
                ChatMessageType.CUSTOM -> {
                    val customBody = it.body as? ChatCustomMessageBody
                    val event = customBody?.event() ?: ""
                    if (event == EaseConstant.MESSAGE_CUSTOM_ALERT){
                        customBody?.params?.let { map->
                            if (map.containsKey(EaseConstant.MESSAGE_CUSTOM_ALERT_TYPE)){
                                if (map[EaseConstant.MESSAGE_CUSTOM_ALERT_TYPE] == EaseConstant.GROUP_WELCOME_MESSAGE){
                                    defaultFilterRow(helper)
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun defaultFilterRow(helper: EaseChatMenuHelper?){
        helper?.setAllItemsVisible(false)
        helper?.clearTopView()
        helper?.findItemVisible(R.id.action_chat_delete,false)
    }
}