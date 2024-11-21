package com.hyphenate.easeui.model

data class ChatUIKitSize(
    var width: Int,
    var height: Int
) {
    fun isEmpty(): Boolean {
        return width <= 0 || height <= 0
    }
}
