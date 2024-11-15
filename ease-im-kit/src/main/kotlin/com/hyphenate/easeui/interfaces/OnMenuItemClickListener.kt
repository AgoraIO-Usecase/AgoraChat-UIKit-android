package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.model.ChatUIKitMenuItem

/**
 * The menu item click listener.
 */
interface OnMenuItemClickListener {
    /**
     * Callback when menu item is clicked.
     * @param item
     * @param position
     */
    fun onMenuItemClick(item: ChatUIKitMenuItem?, position: Int): Boolean
}