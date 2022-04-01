package io.agora.chat.uikit.thread.interfaces;

public interface OnThreadCreatedResultListener {

    /**
     * Thread created successfully
     * @param parentMsgId
     * @param threadId
     * @return
     */
    boolean onThreadCreatedSuccess(String parentMsgId, String threadId);

    /**
     * Failed to create thread
     * @param code
     * @param message
     */
    void onThreadCreatedFail(int code, String message);
}
