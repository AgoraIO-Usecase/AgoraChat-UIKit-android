package io.agora.uikit.feature.chat.interfaces

import android.view.View

/**
 * extend menu item click listener
 */
interface EaseChatExtendMenuItemClickListener {
    /**
     * item click
     * @param itemId
     * @param view
     */
    fun onChatExtendMenuItemClick(itemId: Int, view: View?)
}