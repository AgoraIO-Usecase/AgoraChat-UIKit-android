package com.hyphenate.easeui.feature.chat.reaction.viewholder

import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.UikitItemMessageMenuReactionBinding
import com.hyphenate.easeui.databinding.UikitItemMessageReactionBinding
import com.hyphenate.easeui.model.ChatUIKitReaction

class ChatUIKitReactionNormalViewHolder(
    private val binding: UikitItemMessageReactionBinding
): ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitReaction>(binding = binding) {
    override fun setData(item: ChatUIKitReaction?, position: Int) {
        item?.let { reaction ->
            with(binding) {
                ivEmoji.setImageResource(reaction.icon)
                tvEmojiCount.text = reaction.count.toString()
                root.isSelected = reaction.isAddedBySelf
            }
        }
    }
}