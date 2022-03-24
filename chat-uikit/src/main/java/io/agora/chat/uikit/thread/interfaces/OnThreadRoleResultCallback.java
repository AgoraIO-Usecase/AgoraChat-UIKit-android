package io.agora.chat.uikit.thread.interfaces;

import io.agora.chat.uikit.thread.EaseThreadRole;

/**
 * Use to get thread role in {@link io.agora.chat.uikit.thread.EaseThreadChatFragment}
 */
public interface OnThreadRoleResultCallback {
    /**
     * The role of thread
     * @param role
     */
    void onThreadRole(EaseThreadRole role);
}
