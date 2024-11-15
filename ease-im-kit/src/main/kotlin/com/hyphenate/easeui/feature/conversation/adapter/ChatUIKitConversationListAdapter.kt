package com.hyphenate.easeui.feature.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.conversation.config.ChatUIKitConvItemConfig
import com.hyphenate.easeui.feature.conversation.viewholders.ChatUIKitConvViewType
import com.hyphenate.easeui.feature.conversation.viewholders.ChatUIKitConversationViewHolderFactory
import com.hyphenate.easeui.model.ChatUIKitConversation

/**
 * Adapter for conversation list.
 * @param config Conversation item config.
 */
open class ChatUIKitConversationListAdapter(
    private var config: ChatUIKitConvItemConfig = ChatUIKitConvItemConfig()
): ChatUIKitBaseRecyclerViewAdapter<ChatUIKitConversation>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        return ChatUIKitConversationViewHolderFactory.getViewType(getItem(position))
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int) =
        ChatUIKitConversationViewHolderFactory.createViewHolder(LayoutInflater.from(parent.context), parent,
            ChatUIKitConvViewType.fromValue(viewType), config)

    /**
     * Set conversation item config.
     */
    fun setConversationItemConfig(config: ChatUIKitConvItemConfig) {
        this.config = config
        notifyDataSetChanged()
    }
}