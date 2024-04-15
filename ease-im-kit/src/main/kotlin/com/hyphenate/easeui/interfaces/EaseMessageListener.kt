package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatGroupReadAck
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.ChatMessageListener
import com.hyphenate.easeui.common.ChatMessageReactionChange

open class EaseMessageListener: ChatMessageListener {

    override fun onMessageReceived(messages: MutableList<ChatMessage>?) {}

    override fun onCmdMessageReceived(messages: MutableList<ChatMessage>?) {}

    override fun onMessageRead(messages: MutableList<ChatMessage>?) {}

    override fun onGroupMessageRead(groupReadAcks: MutableList<ChatGroupReadAck>?) {}

    override fun onReadAckForGroupMessageUpdated() {}

    override fun onMessageDelivered(messages: MutableList<ChatMessage>?) {}

    override fun onMessageRecalled(messages: MutableList<ChatMessage>?) {}

    override fun onReactionChanged(messageReactionChangeList: MutableList<ChatMessageReactionChange>?) {}

    override fun onMessageContentChanged(
        messageModified: ChatMessage?,
        operatorId: String?,
        operationTime: Long
    ) {}
}