package io.agora.uikit.feature.chat.chathistory

import android.view.ViewGroup
import io.agora.uikit.common.ChatMessage
import io.agora.uikit.feature.chat.adapter.EaseMessagesAdapter
import io.agora.uikit.feature.chat.chathistory.viewholder.EaseChatHistoryViewHolderFactory
import io.agora.uikit.feature.chat.viewholders.EaseMessageViewType

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
