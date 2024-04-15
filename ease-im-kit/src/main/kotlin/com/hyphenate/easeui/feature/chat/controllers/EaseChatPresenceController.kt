package com.hyphenate.easeui.feature.chat.controllers

import android.content.Context
import com.hyphenate.easeui.feature.chat.widgets.EaseChatLayout
import com.hyphenate.easeui.viewmodel.messages.IChatViewRequest

class EaseChatPresenceController(
    private val context: Context,
    private val chatLayout: EaseChatLayout,
    private val viewModel: IChatViewRequest?,
) {

    fun fetchChatPresence(conversationId:String?){
        conversationId?.let {
            val ids = mutableListOf(it)
            viewModel?.fetchChatPresence(ids)
        }
    }

}