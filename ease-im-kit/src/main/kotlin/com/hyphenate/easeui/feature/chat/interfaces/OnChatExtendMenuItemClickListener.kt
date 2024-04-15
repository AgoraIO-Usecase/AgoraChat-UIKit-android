package com.hyphenate.easeui.feature.chat.interfaces

import android.view.View

interface OnChatExtendMenuItemClickListener {
    /**
     * Extend menu item click event
     * @param view
     * @param itemId
     */
    fun onChatExtendMenuItemClick(view: View?, itemId: Int): Boolean
}