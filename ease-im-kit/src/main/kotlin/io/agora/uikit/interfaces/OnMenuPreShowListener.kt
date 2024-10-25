package io.agora.uikit.interfaces

import io.agora.uikit.menu.EaseMenuHelper

interface OnMenuPreShowListener {
    /**
     * Monitoring before popupMenu display, you can set PopupMenu
     * @param menuHelper [EaseMenuHelper]
     * @param position item position
     */
    fun onMenuPreShow(menuHelper: EaseMenuHelper?, position: Int)
}