package com.hyphenate.easeui.feature.chat.reaction.adapter

import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.model.ChatUIKitReaction

class ChatUIKitMessageReactionAdapter: ChatUIKitBaseRecyclerViewAdapter<ChatUIKitReaction>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        return ChatUIKitMessageReactionViewHolderFactory.getViewType(getItem(position))
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<ChatUIKitReaction> {
        return ChatUIKitMessageReactionViewHolderFactory.createViewHolder(parent, viewType)
    }

}