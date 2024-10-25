package io.agora.uikit.common.impl

import io.agora.uikit.EaseIM
import io.agora.uikit.common.ChatCallback
import io.agora.uikit.common.ChatError
import io.agora.uikit.common.ChatValueCallback


typealias OnSuccess = () -> Unit
typealias OnValueSuccess<T> = (value: T) -> Unit
typealias OnError = (code: Int, error: String?) -> Unit
typealias OnProgress = (progress: Int) -> Unit
class CallbackImpl(private val onSuccess: OnSuccess,
                   private val onError: OnError,
                   private val onProgress: OnProgress = {},
                   private val function: String? = null
): ChatCallback {
    override fun onSuccess() {
        onSuccess.invoke()
        function?.let {
            EaseIM.setEventResultCallback(it,ChatError.EM_NO_ERROR,"")
        }
    }

    override fun onError(code: Int, error: String?) {
        onError.invoke(code, error)
        function?.let {
            EaseIM.setEventResultCallback(it,code,error)
        }
    }

    override fun onProgress(progress: Int, status: String?) {
        onProgress.invoke(progress)
    }
}

class ValueCallbackImpl<T>(private val onSuccess: OnValueSuccess<T>,
                           private val onError: OnError,
                           private val onProgress: OnProgress = {}
): ChatValueCallback<T> {
    override fun onSuccess(value: T) {
        onSuccess.invoke(value)
    }

    override fun onError(error: Int, errorMsg: String?) {
        onError.invoke(error, errorMsg)
    }

    override fun onProgress(progress: Int, status: String?) {
        onProgress.invoke(progress)
    }
}