package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

/**
 * The user listens for modifing messages successfully
 */
public interface OnModifyMessageListener {
    /**
     * modify message success
     * @param messageModified
     */
    void onModifyMessageSuccess(ChatMessage messageModified);

    /**
     * modify message failure
     * @param messageId
     * @param code
     * @param error
     */
    void onModifyMessageFailure(String messageId, int code, String error);
}
