package io.agora.uikit.common.extensions

import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatException
import io.agora.uikit.common.ChatLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.catchChatException(action: suspend FlowCollector<T>.(ChatException) -> Unit): Flow<T> {
    return this.catch { e ->
        if (EaseIM.DEBUG) {
            ChatLog.e("EaseIM", "catchChatException: ${e.message}")
        }
        if (e is ChatException) {
            action.invoke(this, e)
        } else {
            ChatLog.e("catchChatException", "catchChatException: ${e.message}")
        }
    }
}
suspend fun <T> SharedFlow<T>.collectWithCheckErrorCode(checked: (T) -> Unit): Nothing {
    collect{
        if (it is Int && it == ChatError.EM_NO_ERROR) {
            checked.invoke(it)
        } else {
            if (EaseIM.DEBUG) {
                ChatLog.d("flow", "collectWithCheckErrorCode: execute to no check scope")
            }
        }
    }
}
