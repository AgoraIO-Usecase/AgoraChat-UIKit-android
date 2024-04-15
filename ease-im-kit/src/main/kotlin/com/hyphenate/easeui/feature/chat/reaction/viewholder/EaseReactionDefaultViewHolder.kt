package com.hyphenate.easeui.feature.chat.reaction.viewholder

import androidx.viewbinding.ViewBinding
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemMessageMenuReactionBinding
import com.hyphenate.easeui.model.EaseReaction

class EaseReactionDefaultViewHolder(private val binding: EaseItemMessageMenuReactionBinding)
    : EaseBaseRecyclerViewAdapter.ViewHolder<EaseReaction>(binding = binding) {

    override fun initView(viewBinding: ViewBinding?) {
        super.initView(viewBinding)
    }
    override fun setData(item: EaseReaction?, position: Int) {
        item?.let {
            with(binding) {
                ivExpression.setImageResource(item.icon)
                ivExpression.isSelected = it.isAddedBySelf
            }
        }
    }
}