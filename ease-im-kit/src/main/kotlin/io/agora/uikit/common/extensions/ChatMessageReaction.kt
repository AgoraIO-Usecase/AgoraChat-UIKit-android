package io.agora.uikit.common.extensions

import io.agora.uikit.common.ChatMessageReaction
import io.agora.uikit.model.EaseReaction

fun ChatMessageReaction.parse(): EaseReaction {
    return EaseReaction(
        identityCode = reaction,
        emojiText = reaction,
        count = userCount,
        isAddedBySelf = isAddedBySelf
    )
}