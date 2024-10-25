package io.agora.chat.uikit.simple

import io.agora.uikit.common.ChatMessage
import io.agora.uikit.common.extensions.showToast
import io.agora.uikit.feature.chat.EaseChatFragment
import io.agora.uikit.feature.chat.activities.EaseChatActivity
import io.agora.uikit.feature.chat.interfaces.OnMessageForwardCallback
import io.agora.uikit.feature.chat.interfaces.OnSendCombineMessageCallback

class ChatActivity: EaseChatActivity() {
    override fun setChildSettings(builder: EaseChatFragment.Builder) {
        super.setChildSettings(builder)
        builder.setOnMessageForwardCallback(object : OnMessageForwardCallback {
            override fun onForwardSuccess(message: ChatMessage?) {
                mContext.showToast("Message forwarded successfully")
            }

            override fun onForwardError(code: Int, errorMsg: String?) {
                mContext.showToast("Message forwarding failed")
            }
        })
        builder.setOnSendCombineMessageCallback(object : OnSendCombineMessageCallback {
            override fun onSendCombineSuccess(message: ChatMessage?) {
                mContext.showToast("Combine Message successfully")
            }

            override fun onSendCombineError(message: ChatMessage?, code: Int, errorMsg: String?) {
                mContext.showToast("Combine Message failed")
            }
        })
    }
}