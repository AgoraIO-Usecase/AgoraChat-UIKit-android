package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.common.ChatMessageReaction
import com.hyphenate.easeui.model.EaseReaction

fun ChatMessageReaction.parse(): EaseReaction {
    return EaseReaction(
        identityCode = reaction,
        emojiText = reaction,
        count = userCount,
        isAddedBySelf = isAddedBySelf
    )
}