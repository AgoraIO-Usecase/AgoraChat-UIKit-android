package com.hyphenate.easeui.feature.chat.chathistory

import android.view.ViewGroup
import com.hyphenate.easeui.common.ChatMessage
import com.hyphenate.easeui.feature.chat.adapter.ChatUIKitMessagesAdapter
import com.hyphenate.easeui.feature.chat.chathistory.viewholder.ChatUIKitHistoryViewHolderFactory
import com.hyphenate.easeui.feature.chat.viewholders.ChatUIKitMessageViewType

class ChatUIKitHistoryAdapter : ChatUIKitMessagesAdapter() {
    override fun getItemNotEmptyViewType(position: Int): Int {
        return ChatUIKitHistoryViewHolderFactory.getViewType(mData?.get(position))
    }

    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatMessage> {
        return ChatUIKitHistoryViewHolderFactory.createViewHolder(
            parent,
            ChatUIKitMessageViewType.from(viewType)
        )
    }

}
