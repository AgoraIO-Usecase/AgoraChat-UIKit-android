package io.agora.chat.uikit.chat.interfaces;


import io.agora.chat.ChatMessage;

public interface OnAddMsgAttrsBeforeSendEvent {
    /**
     * Set the message properties before sending the message, such as setting ext
     * @param message
     * @return
     */
    void addMsgAttrsBeforeSend(ChatMessage message);
}
