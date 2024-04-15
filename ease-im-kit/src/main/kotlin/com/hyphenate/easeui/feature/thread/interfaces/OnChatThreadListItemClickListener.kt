package com.hyphenate.easeui.feature.thread.interfaces

import android.view.View
import com.hyphenate.easeui.common.ChatThread

interface OnChatThreadListItemClickListener {
    fun onChatThreadItemClick(view: View?,thread: ChatThread)
}