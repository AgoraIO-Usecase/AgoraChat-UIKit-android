package com.hyphenate.easeui.model

import com.hyphenate.easeui.feature.chat.enums.EaseReactionType

/**
 * It is the Emojicon bean for UI sdk.
 * @param icon the icon resource id
 * @param emojiText the text of emoji
 * @param type the type of emoji
 * @param identityCode the identity code of emoji
 * @param bigIcon the big icon resource id
 * @param name the name of emoji
 * @param iconPath the path of icon
 * @param bigIconPath the path of big icon
 */
data class EaseEmojicon @JvmOverloads constructor(
    var icon: Int = 0,
    var emojiText: String = "",
    var type: Type = Type.NORMAL,
    var identityCode: String? = null,
    var bigIcon: Int = -1,
    var name: String? = null,
    var iconPath: String? = null,
    var bigIconPath: String? = null,
) {
    /**
     * Label whether item can be click
     */
    var enableClick = true
    enum class Type {
        /**
         * normal icon, can be input one or more in edit view
         */
        NORMAL,

        /**
         * big icon, send out directly when your press it
         */
        BIG_EXPRESSION
    }
}

fun EaseEmojicon.newEmojiText(codePoint: Int): String {
    return if (Character.charCount(codePoint) == 1) {
        codePoint.toString()
    } else {
        String(Character.toChars(codePoint))
    }
}

/**
 * Change the default emoji to reaction emoji
 */
internal fun EaseEmojicon.toReaction(): EaseReaction {
    return EaseReaction(
        identityCode = emojiText,
        icon = icon,
        emojiText = emojiText,
        isAddedBySelf = false,
        type = EaseReactionType.DEFAULT
    )
}