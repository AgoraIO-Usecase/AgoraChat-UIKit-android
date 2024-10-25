package io.agora.uikit.interfaces

import io.agora.uikit.common.ChatConversationListener

open class EaseConversationListener: ChatConversationListener {
    override fun onConversationUpdate() {}

    override fun onConversationRead(from: String?, to: String?) {}
}