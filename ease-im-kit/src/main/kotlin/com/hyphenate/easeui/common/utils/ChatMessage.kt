package com.hyphenate.easeui.common.utils

import com.hyphenate.easeui.common.ChatClient
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.common.EaseConstant

/**
 * Check if the message id is valid.
 */
internal fun isMessageIdValid(messageId: String?): Boolean {
    // If the message id is null or empty, return true.
    if (messageId.isNullOrEmpty()) {
        return true
    }
    ChatClient.getInstance().chatManager().getMessage(messageId)?.let {
        return true
    } ?: return false
}

/**
 * Create a expression message.
 */
fun createExpressionMessage(toChatUsername: String, expressionName: String?, identityCode: String?): ChatMessage? {
    return ChatMessage.createTextSendMessage("[$expressionName]", toChatUsername)?.let {
        if (!identityCode.isNullOrEmpty()) it.setAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, identityCode)
        it.setAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, true)
        it
    }
}