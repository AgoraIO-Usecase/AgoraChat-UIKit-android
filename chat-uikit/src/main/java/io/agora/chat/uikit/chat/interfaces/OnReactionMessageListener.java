package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface OnReactionMessageListener {

    /**
     * add reaction success
     *
     * @param message
     */
    void addReactionMessageSuccess(ChatMessage message);

    /**
     * add reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    void addReactionMessageFail(ChatMessage message, int code, String error);

    /**
     * remove reaction success
     *
     * @param message
     */
    void removeReactionMessageSuccess(ChatMessage message);

    /**
     * remove reaction fail
     *
     * @param message
     * @param code
     * @param error
     */
    void removeReactionMessageFail(ChatMessage message, int code, String error);

}
