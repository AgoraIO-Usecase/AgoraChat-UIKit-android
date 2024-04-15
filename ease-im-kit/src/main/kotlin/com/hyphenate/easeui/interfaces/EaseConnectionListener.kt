package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatConnectionListener

open class EaseConnectionListener:ChatConnectionListener {

    override fun onConnected() {}

    override fun onDisconnected(p0: Int) {}

    override fun onTokenExpired() {}

    override fun onTokenWillExpire() {}

    override fun onLogout(errorCode: Int) {}

    override fun onLogout(errorCode: Int, info: String?) {}
}