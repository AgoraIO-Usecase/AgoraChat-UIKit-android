package io.agora.uikit.feature.chat.reaction.viewholder

import androidx.viewbinding.ViewBinding
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.databinding.EaseItemMessageMenuReactionBinding
import io.agora.uikit.model.EaseReaction

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