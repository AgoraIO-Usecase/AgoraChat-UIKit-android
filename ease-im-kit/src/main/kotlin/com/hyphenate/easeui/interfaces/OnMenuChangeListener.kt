package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.menu.chat.EaseChatMenuHelper
import com.hyphenate.easeui.model.EaseMenuItem

interface OnMenuChangeListener {
    /**
     * Before showing the Menu
     * @param helper
     * @param message
     */
    fun onPreMenu(helper: EaseChatMenuHelper?, message: ChatMessage?)

    /**
     * Item click
     * @param item
     * @param message
     */
    fun onMenuItemClick(item: EaseMenuItem?, message: ChatMessage?): Boolean

    /**
     * Dismiss event
     */
    fun onDismiss() {}
}