package com.hyphenate.easeui.feature.chat.reaction.viewholder

import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemMessageMenuReactionBinding
import com.hyphenate.easeui.databinding.EaseItemMessageReactionBinding
import com.hyphenate.easeui.model.EaseReaction

class EaseReactionNormalViewHolder(
    private val binding: EaseItemMessageReactionBinding
): EaseBaseRecyclerViewAdapter.ViewHolder<EaseReaction>(binding = binding) {
    override fun setData(item: EaseReaction?, position: Int) {
        item?.let { reaction ->
            with(binding) {
                ivEmoji.setImageResource(reaction.icon)
                tvEmojiCount.text = reaction.count.toString()
                root.isSelected = reaction.isAddedBySelf
            }
        }
    }
}