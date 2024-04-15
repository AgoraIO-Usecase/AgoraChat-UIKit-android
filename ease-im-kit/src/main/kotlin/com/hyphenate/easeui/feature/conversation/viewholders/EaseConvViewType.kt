package com.hyphenate.easeui.feature.conversation.viewholders

enum class EaseConvViewType(val value: Int) {
    VIEW_TYPE_CONVERSATION(0),
    VIEW_TYPE_CONVERSATION_UNKNOWN(-1);

    companion object {
        fun fromValue(value: Int): EaseConvViewType {
            return when(value) {
                0 -> VIEW_TYPE_CONVERSATION
                -1 -> VIEW_TYPE_CONVERSATION_UNKNOWN
                else -> VIEW_TYPE_CONVERSATION_UNKNOWN
            }
        }
    }

}