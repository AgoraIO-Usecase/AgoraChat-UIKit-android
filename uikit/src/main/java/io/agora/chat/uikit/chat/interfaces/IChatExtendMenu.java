package io.agora.chat.uikit.chat.interfaces;

public interface IChatExtendMenu {

    void clear();

    /**
     * Set the order of item
     * @param itemId
     * @param order
     */
    void setMenuOrder(int itemId, int order);

    /**
     * Add new extended functions
     * @param name
     * @param drawableRes
     * @param itemId
     */
    void registerMenuItem(String name, int drawableRes, int itemId);

    /**
     * Add new extended functions
     * @param name
     * @param drawableRes
     * @param itemId
     * @param order
     */
    void registerMenuItem(String name, int drawableRes, int itemId, int order);

    /**
     * Add new extended functions
     * @param nameRes
     * @param drawableRes
     * @param itemId
     */
    void registerMenuItem(int nameRes, int drawableRes, int itemId);

    /**
     * Add new extended functions
     * @param nameRes
     * @param drawableRes
     * @param itemId
     * @param order
     */
    void registerMenuItem(int nameRes, int drawableRes, int itemId, int order);

    /**
     * Set item click listener
     * @param listener
     */
    void setEaseChatExtendMenuItemClickListener(EaseChatExtendMenuItemClickListener listener);
}
