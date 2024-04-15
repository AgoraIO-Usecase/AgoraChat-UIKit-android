package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.model.EaseProfile
import com.hyphenate.easeui.model.EaseUser

/**
 * Convert [EaseUser] to [EaseProfile].
 */
fun EaseUser.toProfile(): EaseProfile {
    return EaseProfile(id = userId, name = nickname, avatar = avatar, remark = remark)
}