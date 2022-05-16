package io.agora.chat.uikit.interfaces;


import io.agora.chat.uikit.menu.EaseMessageMenuHelper;
import io.agora.chat.uikit.menu.EasePopupWindowHelper;
import io.agora.chat.uikit.menu.MenuItemBean;

public interface IPopupWindow {
    void showItemDefaultMenu(boolean showDefault);

    void clearMenu();

    void addItemMenu(MenuItemBean item);

    void addItemMenu(int groupId, int itemId, int order, String title);

    /**
     * Find the menu object, if the id does not exist, return null
     * @param id
     * @return
     */
    MenuItemBean findItem(int id);

    void findItemVisible(int id, boolean visible);

    void setOnPopupWindowItemClickListener(OnMenuChangeListener listener);

    EaseMessageMenuHelper getMenuHelper();
}
