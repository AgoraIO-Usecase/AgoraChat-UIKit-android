package io.agora.chat.uikit.lives;


import io.agora.chat.ChatMessage;

public interface OnSendLiveMessageCallBack {
    /**
     * A successful callback
     *
     * @param message message
     */
    void onSuccess(ChatMessage message);

    /**
     * @param code  error code
     * @param error error info
     */
    void onError(int code, String error);


}
