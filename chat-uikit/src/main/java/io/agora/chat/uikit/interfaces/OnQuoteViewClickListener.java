package io.agora.chat.uikit.interfaces;

import android.view.View;

import io.agora.chat.ChatMessage;

public interface OnQuoteViewClickListener extends IUIKitInterface{

    /**
     * on quote click for quote
     * @param message
     * @return
     */
    void onQuoteViewClick(ChatMessage message);

    /**
     * An error occurred when clicking on the quote view.
     * @param code
     * @param message
     */
    default void onQuoteViewClickError(int code, String message) {}

    /**
     * on long click for quote
     * @param v
     * @param message
     * @return
     */
    default boolean onQuoteViewLongClick(View v, ChatMessage message) {
        return false;
    }
}
