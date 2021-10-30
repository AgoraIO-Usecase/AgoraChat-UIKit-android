package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.ChatMessage;

public interface OnMessageSendCallBack {
    /**
     * 发送消息成功后的回调
     * @param message
     */
    default void onChatSuccess(ChatMessage message){}

    /**
     * 聊天中错误信息
     * @param code
     * @param errorMsg
     */
    void onChatError(int code, String errorMsg);
}
