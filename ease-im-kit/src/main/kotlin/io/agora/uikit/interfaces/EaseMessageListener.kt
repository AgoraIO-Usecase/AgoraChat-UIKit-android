package io.agora.uikit.interfaces

import io.agora.uikit.common.ChatGroupReadAck
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatMessageListener
import io.agora.uikit.common.ChatMessagePinInfo
import io.agora.uikit.common.ChatMessagePinOperation
import io.agora.uikit.common.ChatMessageReactionChange

open class EaseMessageListener: ChatMessageListener {

    override fun onMessageReceived(messages: MutableList<ChatMessage>?) {}

    override fun onCmdMessageReceived(messages: MutableList<ChatMessage>?) {}

    override fun onMessageRead(messages: MutableList<ChatMessage>?) {}

    override fun onGroupMessageRead(groupReadAcks: MutableList<ChatGroupReadAck>?) {}

    override fun onReadAckForGroupMessageUpdated() {}

    override fun onMessageDelivered(messages: MutableList<ChatMessage>?) {}

    override fun onReactionChanged(messageReactionChangeList: MutableList<ChatMessageReactionChange>?) {}

    override fun onMessageContentChanged(
        messageModified: ChatMessage?,
        operatorId: String?,
        operationTime: Long
    ) {}

    override fun onMessagePinChanged(
        messageId: String?,
        conversationId: String?,
        pinOperation: ChatMessagePinOperation?,
        pinInfo: ChatMessagePinInfo?
    ) {
    }
}