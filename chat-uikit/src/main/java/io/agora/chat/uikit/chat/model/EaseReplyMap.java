package io.agora.chat.uikit.chat.model;

/**
 * Map the message type to {@link io.agora.chat.ChatMessage.Type}
 */
public enum EaseReplyMap {
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#TXT}
     */
    txt,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#IMAGE}
     */
    img,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#VIDEO}
     */
    video,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#LOCATION}
     */
    location,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#VOICE}
     */
    audio,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#FILE}
     */
    file,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#CMD}
     */
    cmd,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#CUSTOM}
     */
    custom,
    /**
     * Map to {@link io.agora.chat.ChatMessage.Type#COMBINE}
     */
    combine,
    unknown
}
