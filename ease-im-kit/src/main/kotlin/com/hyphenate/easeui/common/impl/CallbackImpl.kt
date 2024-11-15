package com.hyphenate.easeui.common.impl

import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.common.ChatCallback
import com.hyphenate.easeui.common.ChatError
import com.hyphenate.easeui.common.ChatValueCallback


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
            ChatUIKitClient.setEventResultCallback(it,ChatError.EM_NO_ERROR,"")
        }
    }

    override fun onError(code: Int, error: String?) {
        onError.invoke(code, error)
        function?.let {
            ChatUIKitClient.setEventResultCallback(it,code,error)
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