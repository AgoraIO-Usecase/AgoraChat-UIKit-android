package io.agora.chat.uikit.chat.interfaces;

public interface OnOtherTypingListener {
    /**
     * 用于监听其他人正在数据事件
     * @param action 输入事件 TypingBegin为开始 TypingEnd为结束
     */
    default void onOtherTyping(String action){}
}
