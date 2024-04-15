package com.hyphenate.easeui.feature.conversation.viewholders

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemConversationListBinding
import com.hyphenate.easeui.feature.conversation.config.EaseConvItemConfig
import com.hyphenate.easeui.model.EaseConversation
import com.hyphenate.easeui.model.chatConversation

object EaseConversationViewHolderFactory {
    fun createViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup?,
        viewType: EaseConvViewType = EaseConvViewType.VIEW_TYPE_CONVERSATION,
        style: EaseConvItemConfig = EaseConvItemConfig()
    ): EaseBaseRecyclerViewAdapter.ViewHolder<EaseConversation> {
        return when(viewType) {
            EaseConvViewType.VIEW_TYPE_CONVERSATION -> {
                EaseConversationViewHolder(
                    EaseItemConversationListBinding.inflate(inflater, parent, false),
                    style
                )
            }
            else -> {
                // Return default view holder
                EaseConversationViewHolder(
                    EaseItemConversationListBinding.inflate(inflater, parent, false),
                    style
                )
            }
        }

    }

    fun getViewType(conversation: EaseConversation?): Int {
        return getConversationType(conversation).value
    }

    private fun getConversationType(conversation: EaseConversation?): EaseConvViewType {
        val conv = conversation?.chatConversation()
        return if (conv == null) EaseConvViewType.VIEW_TYPE_CONVERSATION_UNKNOWN
        else EaseConvViewType.VIEW_TYPE_CONVERSATION
    }
}