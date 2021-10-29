package io.agora.chat.uikit.chat.interfaces;


import android.view.View;

/**
 * 用于监听{@link io.agora.chat.uikit.chat.EaseChatLayout}中的变化
 */
public interface OnChatLayoutListener extends OnChatItemClickListener, OnChatInputChangeListener
        , OnOtherTypingListener, OnMessageSendCallBack{

    /**
     * 条目点击
     * @param view
     * @param itemId
     */
    void onChatExtendMenuItemClick(View view, int itemId);
}