package com.hyphenate.easeui.feature.chat.chathistory

import android.view.ViewGroup
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.adapter.EaseMessagesAdapter
import com.hyphenate.easeui.feature.chat.chathistory.viewholder.EaseChatHistoryViewHolderFactory
import com.hyphenate.easeui.feature.chat.viewholders.EaseMessageViewType

class EaseChatHistoryAdapter : EaseMessagesAdapter() {
    override fun getItemNotEmptyViewType(position: Int): Int {
        return EaseChatHistoryViewHolderFactory.getViewType(mData?.get(position))
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        return EaseChatHistoryViewHolderFactory.createViewHolder(
            parent,
            EaseMessageViewType.from(viewType)
        )
    }

}
