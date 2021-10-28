package io.agora.chat.uikit.chat.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;


/**
 * 用于监听{@link io.agora.chat.uikit.chat.EaseChatLayout}中的变化
 */
public interface OnChatLayoutListener {

    /**
     * 点击消息bubble区域
     * @param message
     * @return
     */
    boolean onBubbleClick(ChatMessage message);

    /**
     * 长按消息bubble区域
     * @param v
     * @param message
     * @return
     */
    boolean onBubbleLongClick(View v, ChatMessage message);

    /**
     * 点击头像
     * @param username
     */
    void onUserAvatarClick(String username);

    /**
     * 长按头像
     * @param username
     */
    void onUserAvatarLongClick(String username);

    /**
     * 条目点击
     * @param view
     * @param itemId
     */
    void onChatExtendMenuItemClick(View view, int itemId);

    /**
     * EditText文本变化监听
     * @param s
     * @param start
     * @param before
     * @param count
     */
    void onTextChanged(CharSequence s, int start, int before, int count);

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

    /**
     * 用于监听其他人正在数据事件
     * @param action 输入事件 TypingBegin为开始 TypingEnd为结束
     */
    default void onOtherTyping(String action){}

}