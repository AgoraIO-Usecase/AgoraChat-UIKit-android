package io.agora.uikit.common.extensions

import io.agora.uikit.model.EaseProfile
import io.agora.uikit.model.EaseUser

/**
 * Convert [EaseUser] to [EaseProfile].
 */
fun EaseUser.toProfile(): EaseProfile {
    return EaseProfile(id = userId, name = nickname, avatar = avatar, remark = remark)
}