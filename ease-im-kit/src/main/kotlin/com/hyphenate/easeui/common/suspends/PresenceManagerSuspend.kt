package com.hyphenate.easeui.common.suspends

import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatException
import com.hyphenate.easeui.common.ChatPresence
import com.hyphenate.easeui.common.ChatPresenceManager
import com.hyphenate.easeui.common.impl.CallbackImpl
import com.hyphenate.easeui.common.impl.ValueCallbackImpl
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


/**
 * Suspend method for [ChatPresenceManager.publishExtPresence]
 * @param customStatus Custom Status.
 * @return [ChatError] The error code of the request.
 */
suspend fun ChatPresenceManager.publishExtPresence(customStatus:String):Int{
    return suspendCoroutine { continuation ->
        publishPresence(customStatus, CallbackImpl(
            onSuccess = {
                continuation.resume(ChatError.EM_NO_ERROR)
            },
            onError = {code, message ->
                continuation.resumeWithException(ChatException(code, message))
            })
        )
    }
}


/**
 * Suspend method for [ChatPresenceManager.fetchUserPresenceStatus]
 * @param userIds Subscribe userId list.
 * @return [ChatError] The error code of the request.
 */
suspend fun ChatPresenceManager.fetchUserPresenceStatus(userIds:MutableList<String>):MutableList<ChatPresence>{
    return suspendCoroutine { continuation ->
        val enablePresences = EaseIM.getConfig()?.presencesConfig?.enablePresences ?: false
        if (enablePresences){
            val presence = mutableListOf<ChatPresence>()
            val ids = mutableListOf<String>()
            val presenceInfo = EaseIM.getCache().getPresenceInfo

            if (userIds.size > 0){
                for (userId in userIds) {
                    if (presenceInfo.containsKey(userId)){
                        val cachePresence = EaseIM.getCache().getUserPresence(userId)
                        if (cachePresence == null){
                            ids.add(userId)
                        }else{
                            presence.add(cachePresence)
                        }
                    }else{
                        ids.add(userId)
                    }
                }
            }

            if (ids.isEmpty()){
                continuation.resume(presence)
            }else{
                fetchPresenceStatus(ids, ValueCallbackImpl<MutableList<ChatPresence>>(
                    onSuccess = {
                        presence.addAll(it)
                        it.forEach { presence ->
                            EaseIM.getCache().insertPresences(presence.publisher,presence)
                        }
                        continuation.resume(presence)
                    },
                    onError = {code, message ->
                        continuation.resumeWithException(ChatException(code, message))
                    })
                )
            }
        }
    }
}


/**
 * Suspend method for [ChatPresenceManager.subscribeUsersPresence]
 * @param userIds subscribe userId list.
 * @return [ChatError] The error code of the request.
 */
suspend fun ChatPresenceManager.subscribeUsersPresence(
    userIds:MutableList<String>,expiry:Long
):MutableList<ChatPresence>{
    return suspendCoroutine { continuation ->
        subscribePresences(userIds,expiry, ValueCallbackImpl<MutableList<ChatPresence>>(
            onSuccess = {
                continuation.resume(it)
            },
            onError = {code, message ->
                continuation.resumeWithException(ChatException(code, message))
            })
        )
    }
}


/**
 * Suspend method for [ChatPresenceManager.unSubscribeUsersPresence]
 * @param userIds unSubscribe userId list.
 * @return [ChatError] The error code of the request.
 */
suspend fun ChatPresenceManager.unSubscribeUsersPresence(userIds:MutableList<String>):Int{
    return suspendCoroutine { continuation ->
        unsubscribePresences(userIds, CallbackImpl(
            onSuccess = {
                continuation.resume(ChatError.EM_NO_ERROR)
            },
            onError = {code, message ->
                continuation.resumeWithException(ChatException(code, message))
            })
        )
    }
}