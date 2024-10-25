package io.agora.uikit.common.suspends

import io.agora.uikit.common.ChatConversation
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.ChatSearchDirection
import io.agora.uikit.common.ChatSearchScope
import io.agora.uikit.common.impl.CallbackImpl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Suspend method for [ChatConversation.removeMessagesFromServer].
 * @param messages List of messages to be deleted from the server.
 */
suspend fun ChatConversation.deleteMessage(messages: List<String>): Int =
    suspendCoroutine { continuation ->
        removeMessagesFromServer(messages, CallbackImpl(
            onSuccess = {
                continuation.resume(ChatError.EM_NO_ERROR)
            },
            onError = { code, error ->
                continuation.resumeWithException(ChatException(code, error))
            }
        ))
    }

/**
 * Suspend method for [ChatConversation.searchMessage].
 * @param keywords
 * @param timeStamp
 * @param maxCount
 * @param from
 * @param direction
 */
suspend fun ChatConversation.searchMessage(
    keywords:String,
    timeStamp:Long,
    maxCount:Int,
    from:String?,
    direction:ChatSearchDirection,
    chatScope:ChatSearchScope
):List<ChatMessage> =
    suspendCoroutine { continuation ->
        continuation.resume(searchMsgFromDB(keywords, timeStamp, maxCount, from, direction))
    }