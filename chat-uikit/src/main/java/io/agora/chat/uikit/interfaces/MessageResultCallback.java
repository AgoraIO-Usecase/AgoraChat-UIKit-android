package io.agora.chat.uikit.interfaces;

import io.agora.chat.ChatMessage;

/**
 * message send or download result callback
 */
public interface MessageResultCallback {
    /**
     * message send success
     * @param message
     */
    default void onMessageSuccess(ChatMessage message) {}

    /**
     * message send fail
     * @param message
     * @param code
     * @param error
     */
    default void onMessageError(ChatMessage message, int code, String error) {}

    /**
     * message in sending progress
     * @param message
     * @param progress
     */
    default void onMessageInProgress(ChatMessage message, int progress) {}
}
