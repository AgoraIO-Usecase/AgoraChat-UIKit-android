package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface OnRecallMessageResultListener {
    /**
     * 撤回成功
     * @param message
     */
    void recallSuccess(ChatMessage message);

    /**
     * 撤回失败
     * @param code
     * @param errorMsg
     */
    void recallFail(int code, String errorMsg);
}
