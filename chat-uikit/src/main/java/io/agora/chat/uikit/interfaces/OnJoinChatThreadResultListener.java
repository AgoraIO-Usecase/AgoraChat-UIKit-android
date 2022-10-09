package io.agora.chat.uikit.interfaces;

public interface OnJoinChatThreadResultListener {
    /**
     * Join chat thread success
     * @param threadId
     */
    void joinSuccess(String threadId);

    /**
     * Join chat thread failed
     * @param errorCode
     * @param message
     */
    void joinFailed(int errorCode, String message);
}