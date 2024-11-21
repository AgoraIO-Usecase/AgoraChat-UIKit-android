package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.model.ChatUIKitConversation

interface OnLoadConversationListener {
    fun loadConversationListSuccess(userList: List<ChatUIKitConversation>){}

    fun loadConversationListFail(code: Int, error: String){}
}
