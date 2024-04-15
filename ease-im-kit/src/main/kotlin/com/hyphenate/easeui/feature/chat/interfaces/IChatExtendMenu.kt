package com.hyphenate.easeui.feature.chat.interfaces

interface IChatExtendMenu {
    fun clear()

    /**
     * Set the order of item
     * @param itemId
     * @param order
     */
    fun setMenuOrder(itemId: Int, order: Int)

    /**
     * Add new extended functions
     * @param name
     * @param drawableRes
     * @param itemId
     * @param order
     */
    fun registerMenuItem(name: String?, drawableRes: Int, itemId: Int, order: Int = 0)

    /**
     * Add new extended functions
     * @param nameRes
     * @param drawableRes
     * @param itemId
     * @param order
     */
    fun registerMenuItem(nameRes: Int, drawableRes: Int, itemId: Int, order: Int = 0)

    /**
     * Set item click listener
     * @param listener
     */
    fun setEaseChatExtendMenuItemClickListener(listener: EaseChatExtendMenuItemClickListener?)
}