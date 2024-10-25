package io.agora.uikit.provider

import io.agora.uikit.common.ChatMessage

/**
 * new message options provider
 *
 */
interface EaseSettingsProvider {
    /**
     * Whether to allow message reminders
     * @param message
     * @return
     */
    fun isMsgNotifyAllowed(message: ChatMessage?): Boolean

    /**
     * Whether to set the sound
     * @param message
     * @return
     */
    fun isMsgSoundAllowed(message: ChatMessage?): Boolean

    /**
     * Whether to allow vibration
     * @param message
     * @return
     */
    fun isMsgVibrateAllowed(message: ChatMessage?): Boolean

    /**
     * Whether to use the speaker to play sound
     * @return
     */
    val isSpeakerOpened: Boolean
}