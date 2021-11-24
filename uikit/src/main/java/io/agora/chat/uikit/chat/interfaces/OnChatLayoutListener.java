package io.agora.chat.uikit.chat.interfaces;


import android.view.View;

/**
 * Used to monitor changes in {@link io.agora.chat.uikit.chat.EaseChatLayout}
 */
public interface OnChatLayoutListener extends OnChatItemClickListener, OnChatInputChangeListener
        , OnOtherTypingListener, OnMessageSendCallBack{

    void onChatExtendMenuItemClick(View view, int itemId);
}