package io.agora.uikit.feature.conversation.interfaces

import io.agora.uikit.model.EaseConversation

interface OnLoadConversationListener {
    fun loadConversationListSuccess(userList: List<EaseConversation>){}

    fun loadConversationListFail(code: Int, error: String){}
}
