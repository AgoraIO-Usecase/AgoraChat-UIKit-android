package io.agora.uikit.feature.chat.reaction.viewholder

import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.databinding.EaseItemMessageReactionBinding
import io.agora.uikit.model.EaseReaction

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