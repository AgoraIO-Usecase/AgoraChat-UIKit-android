package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.menu.EaseMenuHelper

interface OnMenuPreShowListener {
    /**
     * Monitoring before popupMenu display, you can set PopupMenu
     * @param menuHelper [EaseMenuHelper]
     * @param position item position
     */
    fun onMenuPreShow(menuHelper: EaseMenuHelper?, position: Int)
}