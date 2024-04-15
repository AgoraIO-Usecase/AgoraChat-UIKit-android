package com.hyphenate.easeui.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import com.hyphenate.easeui.EaseIM
import com.hyphenate.easeui.common.EaseConstant
import com.hyphenate.easeui.common.extensions.hasRoute
import com.hyphenate.easeui.databinding.EaseActivityChatBinding
import com.hyphenate.easeui.feature.chat.EaseChatFragment
import com.hyphenate.easeui.feature.chat.enums.EaseChatType
import com.hyphenate.easeui.feature.chat.search.EaseMessageSearchResultFragment

class EaseMessageSearchResultActivity: EaseChatActivity() {
    override fun getViewBinding(inflater: LayoutInflater): EaseActivityChatBinding? {
        return EaseActivityChatBinding.inflate(inflater)
    }

    override fun setChildSettings(builder: EaseChatFragment.Builder) {
        super.setChildSettings(builder)
        val searchMsgId = intent?.getStringExtra(EaseConstant.EXTRA_SEARCH_MSG_ID)
        builder.setSearchMessageId(searchMsgId)
        builder.setCustomFragment(EaseMessageSearchResultFragment())
    }

    override fun getFragmentTag(): String {
        return "ease_message_search_result_fragment"
    }

    companion object {

        fun actionStart(context: Context, conversationId: String?, chatType: EaseChatType, searchMsgId: String?) {
            Intent(context, EaseMessageSearchResultActivity::class.java).apply {
                putExtra(EaseConstant.EXTRA_CONVERSATION_ID, conversationId)
                putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType.ordinal)
                putExtra(EaseConstant.EXTRA_SEARCH_MSG_ID, searchMsgId)
                EaseIM.getCustomActivityRoute()?.getActivityRoute(this.clone() as Intent)?.let {
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