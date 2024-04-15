package com.hyphenate.easeui.interfaces

import android.view.View
import com.hyphenate.easeui.common.ChatType

/**
 * Forward click listener
 */
interface OnForwardClickListener {
    /**
     * Forward click
     * @param view
     * @param id        The user id or group id
     * @param chatType  The chat type
     */
    fun onForwardClick(view: View?, id: String, chatType: ChatType)
}