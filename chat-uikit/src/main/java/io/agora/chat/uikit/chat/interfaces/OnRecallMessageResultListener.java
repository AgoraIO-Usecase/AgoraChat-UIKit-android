package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface OnRecallMessageResultListener {
    /**
     * Recall successful
     * @param message
     */
    void recallSuccess(ChatMessage message);

    /**
     * Recall failed
     * @param code
     * @param errorMsg
     */
    void recallFail(int code, String errorMsg);
}
