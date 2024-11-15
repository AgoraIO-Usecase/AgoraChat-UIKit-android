package com.hyphenate.easeui.common.extensions

import com.hyphenate.easeui.common.ChatMessageReaction
import com.hyphenate.easeui.model.ChatUIKitReaction

fun ChatMessageReaction.parse(): ChatUIKitReaction {
    return ChatUIKitReaction(
        identityCode = reaction,
        emojiText = reaction,
        count = userCount,
        isAddedBySelf = isAddedBySelf
    )
}