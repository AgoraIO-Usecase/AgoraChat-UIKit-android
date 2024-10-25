package io.agora.uikit.feature.chat.reaction.adapter

import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.model.EaseReaction

class EaseMessageReactionAdapter: EaseBaseRecyclerViewAdapter<EaseReaction>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        return EaseMessageReactionViewHolderFactory.getViewType(getItem(position))
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseReaction> {
        return EaseMessageReactionViewHolderFactory.createViewHolder(parent, viewType)
    }

}