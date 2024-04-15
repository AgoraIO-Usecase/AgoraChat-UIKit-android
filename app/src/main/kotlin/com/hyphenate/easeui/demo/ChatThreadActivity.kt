package com.hyphenate.easeui.demo

import com.hyphenate.easeui.feature.chat.EaseChatFragment
import com.hyphenate.easeui.feature.thread.EaseChatThreadActivity

class ChatThreadActivity:EaseChatThreadActivity() {
    override fun setChildSettings(builder: EaseChatFragment.Builder) {
        super.setChildSettings(builder)
    }
}