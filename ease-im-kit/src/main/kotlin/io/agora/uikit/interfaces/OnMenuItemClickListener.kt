package io.agora.uikit.interfaces

import io.agora.uikit.model.EaseMenuItem

/**
 * The menu item click listener.
 */
interface OnMenuItemClickListener {
    /**
     * Callback when menu item is clicked.
     * @param item
     * @param position
     */
    fun onMenuItemClick(item: EaseMenuItem?, position: Int): Boolean
}