package io.agora.uikit.common.extensions

import io.agora.uikit.EaseIM
import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser
import io.agora.uikit.provider.getSyncUser

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