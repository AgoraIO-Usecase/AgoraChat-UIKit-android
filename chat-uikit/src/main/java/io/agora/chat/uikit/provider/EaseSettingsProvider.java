package io.agora.chat.uikit.provider;


import io.agora.chat.ChatMessage;

/**
 * new message options provider
 *
 */
public interface EaseSettingsProvider {
    /**
     * Whether to allow message reminders
     * @param message
     * @return
     */
    boolean isMsgNotifyAllowed(ChatMessage message);

    /**
     * Whether to set the sound
     * @param message
     * @return
     */
    boolean isMsgSoundAllowed(ChatMessage message);

    /**
     * Whether to allow vibration
     * @param message
     * @return
     */
    boolean isMsgVibrateAllowed(ChatMessage message);

    /**
     * Whether to use the speaker to play sound
     * @return
     */
    boolean isSpeakerOpened();
}