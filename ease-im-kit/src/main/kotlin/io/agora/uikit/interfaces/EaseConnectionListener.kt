package io.agora.uikit.interfaces

import io.agora.uikit.common.ChatConnectionListener

open class EaseConnectionListener:ChatConnectionListener {

    override fun onConnected() {}

    override fun onDisconnected(p0: Int) {}

    override fun onTokenExpired() {}

    override fun onTokenWillExpire() {}

    override fun onLogout(errorCode: Int, info: String?) {}
}