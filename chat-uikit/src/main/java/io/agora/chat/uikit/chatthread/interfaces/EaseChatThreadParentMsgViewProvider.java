package io.agora.chat.uikit.chatthread.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;

public interface EaseChatThreadParentMsgViewProvider {
    /**
     * Get thread parent msg view
     * @param message
     * @return
     */
    View parentMsgView(ChatMessage message);
}
