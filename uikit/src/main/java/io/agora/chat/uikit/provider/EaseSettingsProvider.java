package io.agora.chat.uikit.provider;


import io.agora.chat.ChatMessage;

/**
 * new message options provider
 *
 */
public interface EaseSettingsProvider {
    /**
     * 是否允许消息提醒
     * @param message
     * @return
     */
    boolean isMsgNotifyAllowed(ChatMessage message);

    /**
     * 是否设置声音
     * @param message
     * @return
     */
    boolean isMsgSoundAllowed(ChatMessage message);

    /**
     * 是否允许震动
     * @param message
     * @return
     */
    boolean isMsgVibrateAllowed(ChatMessage message);

    /**
     * 是否使用扬声器播放声音
     * @return
     */
    boolean isSpeakerOpened();
}