package io.agora.chat.uikit.chat.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;

public interface OnChatItemClickListener {
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
}
