package io.agora.uikit.feature.thread.interfaces

import android.view.View
import io.agora.uikit.common.ChatThread

interface OnChatThreadListItemClickListener {
    fun onChatThreadItemClick(view: View?,thread: ChatThread)
}