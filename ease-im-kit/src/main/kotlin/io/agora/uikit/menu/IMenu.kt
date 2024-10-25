package io.agora.uikit.menu

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import io.agora.uikit.interfaces.OnMenuDismissListener
import io.agora.uikit.interfaces.OnMenuItemClickListener
import io.agora.uikit.model.EaseMenuItem

interface IMenu {

    /**
     * Clear menu items.
     */
    fun clear()

    /**
     * Dismiss menu.
     */
    fun dismissMenu()

    /**
     * Rearrange menu items by setting their order.
     */
    fun setMenuOrder(itemId: Int, order: Int)

    /**
     * Register menu item.
     */
    fun registerMenuItem(
        menuId: Int,
        order: Int,
        title: String,
        groupId: Int = 0,
        isVisible: Boolean = true,
        @DrawableRes resourceId: Int = -1,
        @ColorInt titleColor : Int = -1
    )

    /**
     * Register menu item list.
     */
    fun registerMenus(menuItems: List<EaseMenuItem>)

    /**
     * Set menu item click listener.
     */
    fun setOnMenuItemClickListener(listener: OnMenuItemClickListener?)

    /**
     * Set menu dismiss listener.
     */
    fun setOnMenuDismissListener(listener: OnMenuDismissListener?)

}