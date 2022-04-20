package io.agora.chat.uikit.lives;


import io.agora.CallBack;
import io.agora.chat.ChatMessage;

public abstract class OnLiveMessageCallBack implements CallBack {

    /**
     * 为了回调发送的message，不建议使用此回调
     */
    @Deprecated
    @Override
    public void onSuccess() {

    }

    /**
     * 用于发送弹幕消息的成功回调
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
     * 返回消息id，方便根据错误对相应的消息进行删除
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
