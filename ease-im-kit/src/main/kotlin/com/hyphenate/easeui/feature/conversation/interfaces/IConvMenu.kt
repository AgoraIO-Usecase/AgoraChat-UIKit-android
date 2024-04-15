package com.hyphenate.easeui.feature.conversation.interfaces

import com.hyphenate.easeui.interfaces.OnMenuItemClickListener
import com.hyphenate.easeui.interfaces.OnMenuPreShowListener
import com.hyphenate.easeui.menu.EaseMenuHelper

interface IConvMenu {
    /**
     * Clear all menus
     */
    fun clearMenu()

    /**
     * Add item menu
     * @param groupId
     * @param itemId
     * @param order
     * @param title
     */
    fun addItemMenu(itemId: Int, order: Int, title: String, groupId: Int = 0)

    /**
     * Set menu item visibility
     * @param id
     * @param visible
     */
    fun findItemVisible(id: Int, visible: Boolean)

    /**
     * Listening before PopupMenu display, you can set PopupMenu,
     * such as adding menu items, hiding or showing menu items
     * @param preShowListener
     */
    fun setOnMenuPreShowListener(preShowListener: OnMenuPreShowListener?)

    /**
     * Set menu item click listener.
     * @param listener
     */
    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?)

    /**
     * Return to the menu help category
     * @return
     */
    fun getConvMenuHelper(): EaseMenuHelper?
}