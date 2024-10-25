package io.agora.uikit.interfaces

interface OnEventResultListener {
    fun onEventResult(function:String, errorCode: Int, errorMessage: String?)
}