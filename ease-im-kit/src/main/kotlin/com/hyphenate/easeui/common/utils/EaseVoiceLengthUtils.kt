package com.hyphenate.easeui.common.utils

import android.content.Context
import com.hyphenate.easeui.common.extensions.dpToPx
import com.hyphenate.easeui.common.extensions.getScreenInfo

/**
 * use to control voice view's length
 */
object EaseVoiceLengthUtils {
    /**
     * Get the length of the voice
     * @param context
     * @param voiceLength
     * @return
     */
    fun getVoiceLength(context: Context, voiceLength: Int): Int {
        val maxLength: Float =
            context.getScreenInfo()[0] / 4 - 10.dpToPx(context)
        val paddingLeft: Float = if (voiceLength <= 20) {
            voiceLength / 20f * maxLength + 10.dpToPx(context)
        } else {
            maxLength + 10.dpToPx(context)
        }
        return paddingLeft.toInt()
    }
}