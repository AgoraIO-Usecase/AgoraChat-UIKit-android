package io.agora.uikit.feature.chat.activities

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import io.agora.uikit.EaseIM
import io.agora.uikit.common.EaseConstant
import io.agora.uikit.common.extensions.hasRoute
import io.agora.uikit.databinding.EaseActivityChatBinding
import io.agora.uikit.feature.chat.EaseChatFragment
import io.agora.uikit.feature.chat.enums.EaseChatType
import io.agora.uikit.feature.chat.search.EaseMessageSearchResultFragment

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