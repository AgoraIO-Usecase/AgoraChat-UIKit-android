package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.model.EaseMenuItem

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