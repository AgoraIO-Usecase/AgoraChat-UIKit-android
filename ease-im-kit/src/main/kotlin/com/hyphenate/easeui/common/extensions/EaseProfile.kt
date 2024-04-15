package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser
import com.hyphenate.easeui.provider.getSyncUser

/**
 * Convert [EaseProfile] to [EaseUser].
 */
fun EaseProfile.toUser(): EaseUser {
    return EaseUser(userId = id, nickname = name, avatar = avatar, remark = remark)
}

/**
 * Get more information of the user from user provider.
 */
fun EaseProfile.getFullInfo(): EaseProfile {
    if (name.isNullOrEmpty() || avatar.isNullOrEmpty()) {
        EaseIM.getUserProvider()?.getSyncUser(id)?.let {
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