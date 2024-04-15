package com.hyphenate.easeui.common.suspends

import com.hyphenate.easeui.common.ChatConversation
import com.hyphenate.easeui.common.ChatConversationType
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatPushManager
import com.hyphenate.easeui.common.ChatSilentModeParam
import com.hyphenate.easeui.common.ChatSilentModeResult
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.impl.ValueCallbackImpl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Set the silent mode for the conversation.
 * @param conversationId The conversation id.
 * @param chatConversationType The conversation type.
 * @param silentModeParam The silent mode param, see [ChatSilentModeParam].
 * @return The result of setting silent mode. See [ChatSilentModeResult].
 */
suspend fun ChatPushManager.setSilentModeForConversation(conversationId: String,
                                                         chatConversationType: ChatConversationType,
                                                         silentModeParam: ChatSilentModeParam): ChatSilentModeResult {
    return suspendCoroutine { continuation ->
        setSilentModeForConversation(conversationId, chatConversationType, silentModeParam, ValueCallbackImpl<ChatSilentModeResult>(
            onSuccess = {
                continuation.resume(it)
            },
            onError = { error, errorDescription ->
                continuation.resumeWithException(ChatException(error, errorDescription))
            }
        ))
    }
}

/**
 * Clear the setting of offline push notification type for the conversation.
 * After clearing, the conversation follows the Settings of the current logged-in user
 * setSilentModeForAll(SilentModeParam, ValueCallBack).
 * @param conversationId The conversation id.
 * @param chatConversationType The conversation type.
 * @return The error code. See [ChatError].
 */
suspend fun ChatPushManager.clearSilentModeForConversation(conversationId: String,
                                                           chatConversationType: ChatConversationType): Int {
    return suspendCoroutine { continuation ->
        clearRemindTypeForConversation(conversationId, chatConversationType, CallbackImpl(
                onSuccess = {
                    continuation.resume(ChatError.EM_NO_ERROR)
                },
                onError = { error, errorDescription ->
                    continuation.resumeWithException(ChatException(error, errorDescription))
                }
            )
        )
    }
}

/**
 * Obtain the DND settings of specified conversations.
 * @param list The conversation list.
 * @return The result of getting silent mode. See [ChatSilentModeResult].
 */
suspend fun ChatPushManager.getSilentModeOfConversations(list: List<ChatConversation>): Map<String, ChatSilentModeResult> {
    return suspendCoroutine { continuation ->
        getSilentModeForConversations(list.toMutableList(), ValueCallbackImpl(
                onSuccess = {
                    continuation.resume(it)
                },
                onError = { error, errorDescription ->
                    continuation.resumeWithException(ChatException(error, errorDescription))
                }
            )
        )
    }
}