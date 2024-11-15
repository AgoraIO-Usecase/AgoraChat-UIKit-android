package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatContactListener

open class ChatUIKitContactListener: ChatContactListener {
    override fun onContactAdded(username: String?) {}

    override fun onContactDeleted(username: String?) {}

    override fun onContactInvited(username: String?, reason: String?) {}

    override fun onFriendRequestAccepted(username: String?) {}

    override fun onFriendRequestDeclined(username: String?) {}
}