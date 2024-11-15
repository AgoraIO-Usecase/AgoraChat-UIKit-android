package com.hyphenate.easeui.demo

import com.hyphenate.easeui.feature.chat.UIKitChatFragment
import com.hyphenate.easeui.feature.thread.ChatUIKitThreadActivity

class ChatThreadActivity:ChatUIKitThreadActivity() {
    override fun setChildSettings(builder: UIKitChatFragment.Builder) {
        super.setChildSettings(builder)
    }
}