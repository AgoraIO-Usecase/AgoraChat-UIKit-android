package io.agora.chat.uikit.interfaces;

import android.widget.PopupWindow;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;


public interface OnMenuChangeListener {
    /**
     * Before showing the Menu
     * @param helper
     * @param message
     */
    void onPreMenu(EasePopupWindowHelper helper, ChatMessage message);

    /**
     * Item click
     * @param item
     * @param message
     */
    boolean onMenuItemClick(MenuItemBean item, ChatMessage message);

    /**
     * Dismiss event
     * @param menu
     */
    default void onDismiss(PopupWindow menu) {}
}