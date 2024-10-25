package io.agora.uikit.common.extensions

import android.content.Intent

fun Intent.isTargetRoute(clazz: Class<*>): Boolean {
    return component?.className == clazz.name
}

fun Intent.hasRoute(): Boolean {
    return component != null
}