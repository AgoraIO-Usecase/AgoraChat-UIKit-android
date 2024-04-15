package com.hyphenate.easeui.feature.thread.interfaces

interface OnJoinChatThreadResultListener {
    fun joinSuccess(threadId:String?)

    fun joinFailed(code:Int,error:String?)
}