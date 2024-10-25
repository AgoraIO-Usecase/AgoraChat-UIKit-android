package io.agora.uikit.model

/**
 * An entity of group EmojiIcon
 */
data class EaseEmojiconGroupEntity(
    /**
     * Group icon
     */
    var icon: Int = 0,

    /**
     * Emojicon data
     */
    var emojiconList: List<EaseEmojicon?>? = null,

    /**
     * Group name
     */
    var name: String? = null,

    /**
     * Emojicon type
     */
    var type: EaseEmojicon.Type? = null,
)