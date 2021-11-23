package io.agora.chat.uikit.chat.interfaces;

public interface OnOtherTypingListener {
    /**
     * Used to monitor other people's data events
     * @param action Input event TypingBegin is start TypingEnd is finish
     */
    default void onOtherTyping(String action){}
}
