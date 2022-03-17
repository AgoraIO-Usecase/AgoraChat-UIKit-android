package io.agora.chat.uikit.thread.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;

public interface EaseThreadParentMsgViewProvider {
    /**
     * Get thread parent msg view
     * @param message
     * @return
     */
    View parentMsgView(ChatMessage message);
}
