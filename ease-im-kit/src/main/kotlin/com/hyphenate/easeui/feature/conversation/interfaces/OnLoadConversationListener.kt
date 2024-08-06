package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.model.EaseConversation

interface OnLoadConversationListener {
    fun loadConversationListSuccess(userList: List<EaseConversation>){}

    fun loadConversationListFail(code: Int, error: String){}
}
