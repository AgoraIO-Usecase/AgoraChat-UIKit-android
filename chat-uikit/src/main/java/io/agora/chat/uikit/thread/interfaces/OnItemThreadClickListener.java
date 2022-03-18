package io.agora.chat.uikit.thread.interfaces;

import android.view.View;

import io.agora.chat.ChatThread;

/**
 * Thread item click listener
 */
public interface OnItemThreadClickListener {
    /**
     * Thread item click
     * @param view
     * @param thread
     * @param messageId
     */
    void onItemClick(View view, ChatThread thread, String messageId);
}
