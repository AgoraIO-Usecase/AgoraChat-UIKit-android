package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.model.ChatUIKitProfile
import com.hyphenate.easeui.model.ChatUIKitUser
import com.hyphenate.easeui.provider.getSyncUser

/**
 * Convert [ChatUIKitProfile] to [ChatUIKitUser].
 */
fun ChatUIKitProfile.toUser(): ChatUIKitUser {
    return ChatUIKitUser(userId = id, nickname = name, avatar = avatar, remark = remark)
}

/**
 * Get more information of the user from user provider.
 */
fun ChatUIKitProfile.getFullInfo(): ChatUIKitProfile {
    if (name.isNullOrEmpty() || avatar.isNullOrEmpty()) {
        ChatUIKitClient.getUserProvider()?.getSyncUser(id)?.let {
            if (name.isNullOrEmpty()) {
                name = it.name
            }
            if (avatar.isNullOrEmpty()) {
                avatar = it.avatar
            }
        }
    }
    return this
}