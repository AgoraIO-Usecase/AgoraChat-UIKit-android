package io.agora.uikit.model

import io.agora.uikit.R
import io.agora.uikit.common.helper.EaseEmojiHelper
import io.agora.uikit.feature.chat.enums.EaseReactionType

object EaseDefaultEmojiIconData {
    private val emojis = arrayOf<String>(
        EaseEmojiHelper.ee_1,
        EaseEmojiHelper.ee_2,
        EaseEmojiHelper.ee_3,
        EaseEmojiHelper.ee_4,
        EaseEmojiHelper.ee_5,
        EaseEmojiHelper.ee_6,
        EaseEmojiHelper.ee_7,
        EaseEmojiHelper.ee_8,
        EaseEmojiHelper.ee_9,
        EaseEmojiHelper.ee_10,
        EaseEmojiHelper.ee_11,
        EaseEmojiHelper.ee_12,
        EaseEmojiHelper.ee_13,
        EaseEmojiHelper.ee_14,
        EaseEmojiHelper.ee_15,
        EaseEmojiHelper.ee_16,
        EaseEmojiHelper.ee_17,
        EaseEmojiHelper.ee_18,
        EaseEmojiHelper.ee_19,
        EaseEmojiHelper.ee_20,
        EaseEmojiHelper.ee_21,
        EaseEmojiHelper.ee_22,
        EaseEmojiHelper.ee_23,
        EaseEmojiHelper.ee_24,
        EaseEmojiHelper.ee_25,
        EaseEmojiHelper.ee_26,
        EaseEmojiHelper.ee_27,
        EaseEmojiHelper.ee_28,
        EaseEmojiHelper.ee_29,
        EaseEmojiHelper.ee_30,
        EaseEmojiHelper.ee_31,
        EaseEmojiHelper.ee_32,
        EaseEmojiHelper.ee_33,
        EaseEmojiHelper.ee_34,
        EaseEmojiHelper.ee_35,
        EaseEmojiHelper.ee_36,
        EaseEmojiHelper.ee_37,
        EaseEmojiHelper.ee_38,
        EaseEmojiHelper.ee_39,
        EaseEmojiHelper.ee_40,
        EaseEmojiHelper.ee_41,
        EaseEmojiHelper.ee_42,
        EaseEmojiHelper.ee_43,
        EaseEmojiHelper.ee_44,
        EaseEmojiHelper.ee_45,
        EaseEmojiHelper.ee_46,
        EaseEmojiHelper.ee_47,
        EaseEmojiHelper.ee_48,
        EaseEmojiHelper.ee_49,
        EaseEmojiHelper.ee_50,
        EaseEmojiHelper.ee_51,
        EaseEmojiHelper.ee_52,
    )
    val defaultReactions = arrayOf(
        EaseReaction(EaseEmojiHelper.ee_33, R.drawable.emoji_33, type = EaseReactionType.DEFAULT),
        EaseReaction(EaseEmojiHelper.ee_40, R.drawable.emoji_40, type = EaseReactionType.DEFAULT),
        EaseReaction(EaseEmojiHelper.ee_3, R.drawable.emoji_3, type = EaseReactionType.DEFAULT),
        EaseReaction(EaseEmojiHelper.ee_22, R.drawable.emoji_22, type = EaseReactionType.DEFAULT),
        EaseReaction(EaseEmojiHelper.ee_11, R.drawable.emoji_11, type = EaseReactionType.DEFAULT),
        EaseReaction(EaseEmojiHelper.ee_49, R.drawable.emoji_49, type = EaseReactionType.DEFAULT)
    )
    private val icons = intArrayOf(
        R.drawable.emoji_1,
        R.drawable.emoji_2,
        R.drawable.emoji_3,
        R.drawable.emoji_4,
        R.drawable.emoji_5,
        R.drawable.emoji_6,
        R.drawable.emoji_7,
        R.drawable.emoji_8,
        R.drawable.emoji_9,
        R.drawable.emoji_10,
        R.drawable.emoji_11,
        R.drawable.emoji_12,
        R.drawable.emoji_13,
        R.drawable.emoji_14,
        R.drawable.emoji_15,
        R.drawable.emoji_16,
        R.drawable.emoji_17,
        R.drawable.emoji_18,
        R.drawable.emoji_19,
        R.drawable.emoji_20,
        R.drawable.emoji_21,
        R.drawable.emoji_22,
        R.drawable.emoji_23,
        R.drawable.emoji_24,
        R.drawable.emoji_25,
        R.drawable.emoji_26,
        R.drawable.emoji_27,
        R.drawable.emoji_28,
        R.drawable.emoji_29,
        R.drawable.emoji_30,
        R.drawable.emoji_31,
        R.drawable.emoji_32,
        R.drawable.emoji_33,
        R.drawable.emoji_34,
        R.drawable.emoji_35,
        R.drawable.emoji_36,
        R.drawable.emoji_37,
        R.drawable.emoji_38,
        R.drawable.emoji_39,
        R.drawable.emoji_40,
        R.drawable.emoji_41,
        R.drawable.emoji_42,
        R.drawable.emoji_43,
        R.drawable.emoji_44,
        R.drawable.emoji_45,
        R.drawable.emoji_46,
        R.drawable.emoji_47,
        R.drawable.emoji_48,
        R.drawable.emoji_49,
        R.drawable.emoji_50,
        R.drawable.emoji_51,
        R.drawable.emoji_52
    )
    val data = createData()
    private fun createData(): Array<EaseEmojicon?> {
        val data = arrayOfNulls<EaseEmojicon>(icons.size)
        for (i in icons.indices) {
            data[i] = EaseEmojicon(icons[i], emojis[i], EaseEmojicon.Type.NORMAL)
        }
        return data
    }

    val mapData = createMapData()
    private fun createMapData(): Map<String, Int> {
        val mapData = mutableMapOf<String, Int>()
        for (i in icons.indices) {
            mapData[emojis[i]] = icons[i]
        }
        return mapData
    }
}