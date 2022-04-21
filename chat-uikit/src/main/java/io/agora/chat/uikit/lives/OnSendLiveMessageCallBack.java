package io.agora.chat.uikit.lives;


import io.agora.CallBack;
import io.agora.chat.ChatMessage;

public abstract class OnSendLiveMessageCallBack implements CallBack {

    @Override
    public void onSuccess() {

    }

    /**
     * A successful callback for sending a bullet screen message
     *
     * @param message
     */
    public abstract void onSuccess(ChatMessage message);

    /**
     * @param code
     * @param error
     * @see #onError(String, int, String)
     */
    @Deprecated
    @Override
    public void onError(int code, String error) {

    }

    /**
     * Returns the message id, which is convenient for deleting the corresponding message according to the error
     *
     * @param messageId
     * @param code
     * @param error
     */
    public void onError(String messageId, int code, String error) {

    }

    @Override
    public void onProgress(int i, String s) {

    }
}
