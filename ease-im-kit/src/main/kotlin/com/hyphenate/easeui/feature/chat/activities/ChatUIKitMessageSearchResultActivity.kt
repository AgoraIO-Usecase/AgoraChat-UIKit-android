package com.hyphenate.easeui.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.hyphenate.easeui.ChatUIKitClient
import com.hyphenate.easeui.common.ChatUIKitConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.UikitActivityChatBinding
import com.hyphenate.easeui.feature.chat.UIKitChatFragment
import com.hyphenate.easeui.feature.chat.enums.ChatUIKitType
import com.hyphenate.easeui.feature.chat.search.ChatUIKitMessageSearchResultFragment

class ChatUIKitMessageSearchResultActivity: UIKitChatActivity() {
    override fun getViewBinding(inflater: LayoutInflater): UikitActivityChatBinding? {
        return UikitActivityChatBinding.inflate(inflater)
    }

    override fun setChildSettings(builder: UIKitChatFragment.Builder) {
        super.setChildSettings(builder)
        val searchMsgId = intent?.getStringExtra(ChatUIKitConstant.EXTRA_SEARCH_MSG_ID)
        builder.setSearchMessageId(searchMsgId)
        builder.setCustomFragment(ChatUIKitMessageSearchResultFragment())
    }

    override fun getFragmentTag(): String {
        return "ease_message_search_result_fragment"
    }

    companion object {

        fun actionStart(context: Context, conversationId: String?, chatType: ChatUIKitType, searchMsgId: String?) {
            Intent(context, ChatUIKitMessageSearchResultActivity::class.java).apply {
                putExtra(ChatUIKitConstant.EXTRA_CONVERSATION_ID, conversationId)
                putExtra(ChatUIKitConstant.EXTRA_CHAT_TYPE, chatType.ordinal)
                putExtra(ChatUIKitConstant.EXTRA_SEARCH_MSG_ID, searchMsgId)
                ChatUIKitClient.getCustomActivityRoute()?.getActivityRoute(this.clone() as Intent)?.let {
                    if (it.hasRoute()) {
                        context.startActivity(it)
                        return
                    }
                }
                context.startActivity(this)
            }
        }
    }

}