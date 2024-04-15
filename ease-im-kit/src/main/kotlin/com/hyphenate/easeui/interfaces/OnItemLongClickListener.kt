package com.hyphenate.easeui.interfaces

import android.view.View

/**
 * Item long click listener
 */
interface OnItemLongClickListener {
    /**
     * Item long click
     * @param view
     * @param position
     */
    fun onItemLongClick(view: View?, position: Int): Boolean
}