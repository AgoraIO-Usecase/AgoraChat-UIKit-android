package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.ChatMessage;

public interface OnMessageSendCallBack {
    /**
     * Callback after the message is sent successfully
     * @param message
     */
    default void onSuccess(ChatMessage message){}

    /**
     * Wrong message in chat
     * @param code
     * @param errorMsg
     */
    void onError(int code, String errorMsg);
}
