package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.model.ChatUIKitProfile
import com.hyphenate.easeui.model.ChatUIKitUser

/**
 * Convert [ChatUIKitUser] to [ChatUIKitProfile].
 */
fun ChatUIKitUser.toProfile(): ChatUIKitProfile {
    return ChatUIKitProfile(id = userId, name = nickname, avatar = avatar, remark = remark)
}