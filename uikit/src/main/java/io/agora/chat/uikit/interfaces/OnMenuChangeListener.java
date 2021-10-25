package io.agora.chat.uikit.interfaces;

import android.widget.PopupWindow;

import io.agora.chat.ChatMessage;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;


/**
 * {@link EasePopupWindowHelper}中的条目点击事件
 */
public interface OnMenuChangeListener {
    /**
     * 展示Menu之前
     * @param helper
     * @param message
     */
    void onPreMenu(EasePopupWindowHelper helper, ChatMessage message);

    /**
     * 点击条目
     * @param item
     * @param message
     */
    boolean onMenuItemClick(MenuItemBean item, ChatMessage message);

    /**
     * 消失
     * @param menu
     */
    default void onDismiss(PopupWindow menu) {}
}