package io.agora.uikit.feature.thread.interfaces

import android.view.View
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatThread

interface OnMessageChatThreadClickListener {
    fun onThreadViewItemClick(view:View, thread:ChatThread?,topicMsg:ChatMessage)
}