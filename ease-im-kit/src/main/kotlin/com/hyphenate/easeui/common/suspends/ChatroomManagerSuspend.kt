package com.hyphenate.easeui.common.suspends

import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.Chatroom
import com.hyphenate.easeui.common.ChatroomManager
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.impl.ValueCallbackImpl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Suspend method for [ChatroomManager.joinChatRoom]
 * @param roomId The id of the chatroom.
 * @return [Chatroom] The chatroom that you joined.
 */
suspend fun ChatroomManager.joinChatroom(roomId: String): Chatroom {
    return suspendCoroutine { continuation ->
        joinChatRoom(roomId, ValueCallbackImpl<Chatroom>(
                onSuccess = { value ->
                    continuation.resume(value)
                },
                onError = { code, message ->
                    continuation.resumeWithException(ChatException(code, message))
                }
            )
        )
    }
}

/**
 * Suspend method for [ChatroomManager.leaveChatRoom]
 * @param roomId The id of the chatroom.
 * @return [ChatError] The error code of the request.
 */
suspend fun ChatroomManager.leaveChatroom(roomId: String): Int {
    return suspendCoroutine { continuation ->
        leaveChatRoom(roomId, CallbackImpl(
                onSuccess = {
                    continuation.resume(ChatError.EM_NO_ERROR)
                },
                onError = { code, message ->
                    continuation.resumeWithException(ChatException(code, message))
                }
            )
        )
    }
}