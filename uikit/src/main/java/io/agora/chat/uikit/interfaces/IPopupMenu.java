package io.agora.chat.uikit.interfaces;


import io.agora.chat.uikit.menu.EasePopupMenuHelper;
import io.agora.chat.uikit.menu.OnPopupMenuDismissListener;
import io.agora.chat.uikit.menu.OnPopupMenuItemClickListener;
import io.agora.chat.uikit.menu.OnPopupMenuPreShowListener;

public interface IPopupMenu {
    /**
     * Clear all menus
     */
    void clearMenu();

    /**
     * Add item menu
     * @param groupId
     * @param itemId
     * @param order
     * @param title
     */
    void addItemMenu(int groupId, int itemId, int order, String title);

    /**
     * Set menu item visibility
     * @param id
     * @param visible
     */
    void findItemVisible(int id, boolean visible);

    /**
     * Listening before PopupMenu display, you can set PopupMenu,
     * such as adding menu items, hiding or showing menu items
     * @param preShowListener
     */
    void setOnPopupMenuPreShowListener(OnPopupMenuPreShowListener preShowListener);

    void setOnPopupMenuItemClickListener(OnPopupMenuItemClickListener listener);

    void setOnPopupMenuDismissListener(OnPopupMenuDismissListener listener);

    /**
     * Return to the menu help category
     * @return
     */
    EasePopupMenuHelper getMenuHelper();
}
