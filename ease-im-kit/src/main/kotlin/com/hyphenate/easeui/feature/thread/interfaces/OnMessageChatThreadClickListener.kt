package com.hyphenate.easeui.feature.thread.interfaces

import android.view.View
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatThread

interface OnMessageChatThreadClickListener {
    fun onThreadViewItemClick(view:View, thread:ChatThread?,topicMsg:ChatMessage)
}