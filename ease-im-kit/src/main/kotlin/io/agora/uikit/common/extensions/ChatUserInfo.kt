package io.agora.uikit.common.extensions

import io.agora.uikit.common.ChatUserInfo
import io.agora.uikit.model.EaseUser

/**
 * It is a file used to convert Chat SDK classes into easeui SDK classes.
 */

/**
 * Convert [ChatUserInfo] to [EaseUser].
 */
internal fun ChatUserInfo.parse() = EaseUser(
    userId = userId,
    nickname = nickname,
    avatar = avatarUrl,
    email = email,
    gender = gender,
    sign = signature,
    birth = birth,
    ext = ext,
)