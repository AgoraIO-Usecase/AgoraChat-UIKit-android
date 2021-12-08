package io.agora.chat.uikit.chat.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;

public interface OnMessageItemClickListener {
    /**
     * Click on the message bubble area
     * @param message
     * @return
     */
    boolean onBubbleClick(ChatMessage message);

    /**
     * Long press the message bubble area
     * @param v
     * @param message
     * @return
     */
    boolean onBubbleLongClick(View v, ChatMessage message);

    /**
     * Click on the avatar
     * @param username
     */
    void onUserAvatarClick(String username);

    /**
     * Long press on the avatar
     * @param username
     */
    void onUserAvatarLongClick(String username);
}
