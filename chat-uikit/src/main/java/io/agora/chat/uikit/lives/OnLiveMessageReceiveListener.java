package io.agora.chat.uikit.lives;


import io.agora.chat.ChatMessage;

/**
 * 定义接收到的消息类型
 */
public interface OnLiveMessageReceiveListener {
    /**
     * 接收到礼物消息
     *
     * @param message
     */
    void onReceiveGiftMsg(ChatMessage message);

    /**
     * 接收到点赞消息
     *
     * @param message
     */
    void onReceivePraiseMsg(ChatMessage message);

    /**
     * 接收到弹幕消息
     *
     * @param message
     */
    void onReceiveBarrageMsg(ChatMessage message);
}
