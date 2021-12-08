package io.agora.chat.uikit.chat.interfaces;

public interface OnPeerTypingListener {
    /**
     * Used to monitor peer's data events
     * @param action Input event TypingBegin is start TypingEnd is finish
     */
    default void onPeerTyping(String action){}
}
