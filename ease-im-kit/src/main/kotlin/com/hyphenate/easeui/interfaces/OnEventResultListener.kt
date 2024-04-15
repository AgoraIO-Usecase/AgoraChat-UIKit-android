package com.hyphenate.easeui.interfaces

interface OnEventResultListener {
    fun onEventResult(function:String, errorCode: Int, errorMessage: String?)
}