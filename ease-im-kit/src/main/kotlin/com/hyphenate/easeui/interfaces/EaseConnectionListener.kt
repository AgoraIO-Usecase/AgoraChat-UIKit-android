package com.hyphenate.easeui.interfaces

import com.hyphenate.easeui.common.ChatConnectionListener
import com.hyphenate.easeui.common.ChatLoginExtensionInfo

open class EaseConnectionListener:ChatConnectionListener {

    override fun onConnected() {}

    override fun onDisconnected(p0: Int) {}

    override fun onTokenExpired() {}

    override fun onTokenWillExpire() {}

    override fun onLogout(errorCode: Int, info: ChatLoginExtensionInfo?) {}
}