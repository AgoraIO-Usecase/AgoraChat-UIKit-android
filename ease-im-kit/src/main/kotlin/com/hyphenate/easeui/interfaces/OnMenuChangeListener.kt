package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.menu.chat.ChatUIKitChatMenuHelper
import com.hyphenate.easeui.model.ChatUIKitMenuItem

interface OnMenuChangeListener {
    /**
     * Before showing the Menu
     * @param helper
     * @param message
     */
    fun onPreMenu(helper: ChatUIKitChatMenuHelper?, message: ChatMessage?)

    /**
     * Item click
     * @param item
     * @param message
     */
    fun onMenuItemClick(item: ChatUIKitMenuItem?, message: ChatMessage?): Boolean

    /**
     * Dismiss event
     */
    fun onDismiss() {}
}