package io.agora.chat.uikit.chatthread.interfaces;

import android.view.View;

import io.agora.chat.ChatThread;

/**
 * Thread item click listener
 */
public interface OnItemChatThreadClickListener {
    /**
     * Thread item click
     * @param view
     * @param thread
     * @param messageId
     */
    void onItemClick(View view, ChatThread thread, String messageId);
}
