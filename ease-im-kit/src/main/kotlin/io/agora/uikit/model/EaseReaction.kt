package io.agora.uikit.model

import io.agora.uikit.feature.chat.enums.EaseReactionType

data class EaseReaction (
    var identityCode: String? = null,
    var icon: Int = 0,
    var emojiText: String? = null,
    var count: Int = 0,
    var isAddedBySelf: Boolean = false,
    var type: EaseReactionType = EaseReactionType.NORMAL
)
