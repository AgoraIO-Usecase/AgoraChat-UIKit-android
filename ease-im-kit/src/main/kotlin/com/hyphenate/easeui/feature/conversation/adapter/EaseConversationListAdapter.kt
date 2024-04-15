package com.hyphenate.easeui.feature.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.feature.conversation.config.EaseConvItemConfig
import com.hyphenate.easeui.feature.conversation.viewholders.EaseConvViewType
import com.hyphenate.easeui.feature.conversation.viewholders.EaseConversationViewHolderFactory
import com.hyphenate.easeui.model.EaseConversation

/**
 * Adapter for conversation list.
 * @param config Conversation item config.
 */
open class EaseConversationListAdapter(
    private var config: EaseConvItemConfig = EaseConvItemConfig()
): EaseBaseRecyclerViewAdapter<EaseConversation>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        return EaseConversationViewHolderFactory.getViewType(getItem(position))
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int) =
        EaseConversationViewHolderFactory.createViewHolder(LayoutInflater.from(parent.context), parent,
            EaseConvViewType.fromValue(viewType), config)

    /**
     * Set conversation item config.
     */
    fun setConversationItemConfig(config: EaseConvItemConfig) {
        this.config = config
        notifyDataSetChanged()
    }
}