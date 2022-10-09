package io.agora.chat.uikit.chat.interfaces;

import io.agora.chat.uikit.chat.EaseChatLayout;
import io.agora.chat.uikit.interfaces.OnTitleBarFinishInflateListener;
import io.agora.chat.uikit.widget.EaseTitleBar;

public interface OnChatLayoutFinishInflateListener extends OnTitleBarFinishInflateListener {

    /**
     * Callback method after EaseChatLayout initialization
     * @param chatLayout
     */
    default void onChatListFinishInflate(EaseChatLayout chatLayout) {}
}
