package io.agora.chat.uikit.interfaces;

import io.agora.chat.uikit.widget.EaseTitleBar;

public interface OnTitleBarFinishInflateListener {
    /**
     * Callback method after TitleBar initialization
     * @param titleBar
     */
    default void onTitleBarFinishInflate(EaseTitleBar titleBar) {}
}
