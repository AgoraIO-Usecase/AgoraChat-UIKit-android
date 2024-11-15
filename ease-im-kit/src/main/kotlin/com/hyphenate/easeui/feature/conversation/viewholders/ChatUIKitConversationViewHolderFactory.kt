package com.hyphenate.easeui.feature.conversation.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.UikitItemConversationListBinding
import com.hyphenate.easeui.feature.conversation.config.ChatUIKitConvItemConfig
import com.hyphenate.easeui.model.ChatUIKitConversation
import com.hyphenate.easeui.model.chatConversation

object ChatUIKitConversationViewHolderFactory {
    fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        viewType: ChatUIKitConvViewType = ChatUIKitConvViewType.VIEW_TYPE_CONVERSATION,
        style: ChatUIKitConvItemConfig = ChatUIKitConvItemConfig()
    ): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitConversation> {
        return when(viewType) {
            ChatUIKitConvViewType.VIEW_TYPE_CONVERSATION -> {
                ChatUIKitConversationViewHolder(
                    UikitItemConversationListBinding.inflate(inflater, parent, false),
                    style
                )
            }
            else -> {
                // Return default view holder
                ChatUIKitConversationViewHolder(
                    UikitItemConversationListBinding.inflate(inflater, parent, false),
                    style
                )
            }
        }

    }

    fun getViewType(conversation: ChatUIKitConversation?): Int {
        return getConversationType(conversation).value
    }

    private fun getConversationType(conversation: ChatUIKitConversation?): ChatUIKitConvViewType {
        val conv = conversation?.chatConversation()
        return if (conv == null) ChatUIKitConvViewType.VIEW_TYPE_CONVERSATION_UNKNOWN
        else ChatUIKitConvViewType.VIEW_TYPE_CONVERSATION
    }
}