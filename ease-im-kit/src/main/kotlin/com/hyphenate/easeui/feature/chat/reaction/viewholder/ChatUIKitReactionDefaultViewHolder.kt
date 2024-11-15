package com.hyphenate.easeui.feature.chat.reaction.viewholder

import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.UikitItemMessageMenuReactionBinding
import com.hyphenate.easeui.model.ChatUIKitReaction

class ChatUIKitReactionDefaultViewHolder(private val binding: UikitItemMessageMenuReactionBinding)
    : ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitReaction>(binding = binding) {

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
    }
    override fun setData(item: ChatUIKitReaction?, position: Int) {
        item?.let {
            with(binding) {
                ivExpression.setImageResource(item.icon)
                ivExpression.isSelected = it.isAddedBySelf
            }
        }
    }
}