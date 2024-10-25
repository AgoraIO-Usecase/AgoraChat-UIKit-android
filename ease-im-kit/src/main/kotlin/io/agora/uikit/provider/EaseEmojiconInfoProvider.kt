package io.agora.uikit.provider

import io.agora.uikit.model.EaseEmojicon

/**
 * Custom emoji icon provider
 */
interface EaseEmojiconInfoProvider {
    /**
     * return EaseEmojicon for input emojiconIdentityCode
     * @param emojiconIdentityCode
     * @return
     */
    fun getEmojiconInfo(emojiconIdentityCode: String?): EaseEmojicon?

    /**
     * get Emojicon map, key is the text of emoji, value is the resource id or local path of emoji icon(can't be URL on internet)
     * @return
     */
    val textEmojiconMapping: Map<String?, Any?>?
}