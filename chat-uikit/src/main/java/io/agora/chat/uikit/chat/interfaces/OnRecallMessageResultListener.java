package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface OnRecallMessageResultListener {
    /**
     * Recall successful
     * @param originalMessage The message was unsent
     * @param notification  The notification message
     */
    void recallSuccess(ChatMessage originalMessage, ChatMessage notification);

    /**
     * Recall failed
     * @param code
     * @param errorMsg
     */
    void recallFail(int code, String errorMsg);
}
