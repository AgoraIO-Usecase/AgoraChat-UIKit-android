package io.agora.uikit.feature.conversation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.feature.conversation.config.EaseConvItemConfig
import io.agora.uikit.feature.conversation.viewholders.EaseConvViewType
import io.agora.uikit.feature.conversation.viewholders.EaseConversationViewHolderFactory
import io.agora.uikit.model.EaseConversation

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