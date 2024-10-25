package io.agora.uikit.feature.thread.interfaces

import io.agora.uikit.common.ChatMessage

interface IMessageThread {
    fun setupWithMessage(message:ChatMessage)

    fun showThread()

    fun setThreadEventListener(listener: OnMessageChatThreadClickListener?)
}