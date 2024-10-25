package io.agora.uikit.interfaces

import android.view.View

/**
 * Item click listener
 */
interface OnItemClickListener {
    /**
     * item click
     * @param view
     * @param position
     */
    fun onItemClick(view: View?, position: Int)
}