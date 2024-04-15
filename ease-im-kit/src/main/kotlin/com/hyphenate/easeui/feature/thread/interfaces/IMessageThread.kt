package com.hyphenate.easeui.feature.thread.interfaces

import com.hyphenate.easeui.common.ChatMessage

interface IMessageThread {
    fun setupWithMessage(message:ChatMessage)

    fun showThread()

    fun setThreadEventListener(listener: OnMessageChatThreadClickListener?)
}