package com.hyphenate.easeui.feature.chat.reaction.adapter

import android.view.ViewGroup
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.model.EaseReaction

class EaseMessageReactionAdapter: EaseBaseRecyclerViewAdapter<EaseReaction>() {

    override fun getItemNotEmptyViewType(position: Int): Int {
        return EaseMessageReactionViewHolderFactory.getViewType(getItem(position))
    }
    override fun getViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<EaseReaction> {
        return EaseMessageReactionViewHolderFactory.createViewHolder(parent, viewType)
    }

}