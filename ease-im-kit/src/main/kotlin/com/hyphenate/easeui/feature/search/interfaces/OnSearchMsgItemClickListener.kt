package com.hyphenate.easeui.feature.search.interfaces

import android.view.View
import com.hyphenate.easeui.common.ChatMessage

interface OnSearchMsgItemClickListener {
    /**
     * item click
     * @param view
     * @param position
     * @param msg
     */
    fun onSearchItemClick(view: View?, position: Int, msg: ChatMessage)
}