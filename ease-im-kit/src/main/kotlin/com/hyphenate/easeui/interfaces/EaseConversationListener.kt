package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatConversationListener

open class EaseConversationListener: ChatConversationListener {
    override fun onConversationUpdate() {}

    override fun onConversationRead(from: String?, to: String?) {}
}