package io.agora.uikit.interfaces

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.menu.chat.EaseChatMenuHelper
import io.agora.uikit.model.EaseMenuItem

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