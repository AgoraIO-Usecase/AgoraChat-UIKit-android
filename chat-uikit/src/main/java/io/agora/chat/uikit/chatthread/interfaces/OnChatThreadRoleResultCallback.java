package io.agora.chat.uikit.chatthread.interfaces;

import io.agora.chat.uikit.chatthread.EaseChatThreadFragment;
import io.agora.chat.uikit.chatthread.EaseChatThreadRole;

/**
 * Use to get thread role in {@link EaseChatThreadFragment}
 */
public interface OnChatThreadRoleResultCallback {
    /**
     * The role of thread
     * @param role
     */
    void onThreadRole(EaseChatThreadRole role);
}
