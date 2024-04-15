package com.hyphenate.easeui.feature.chat.reaction.viewholder

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.EaseBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.EaseItemMessageMenuReactionBinding
import com.hyphenate.easeui.model.EaseReaction

class EaseReactionAddViewHolder(private val binding: EaseItemMessageMenuReactionBinding)
    : EaseBaseRecyclerViewAdapter.ViewHolder<EaseReaction>(binding = binding) {

    override fun setData(item: EaseReaction?, position: Int) {
        item?.let {
            with(binding) {
                ivExpression.background = null
                ivExpression.setImageResource(item.icon)
                ivExpression.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.ease_color_on_background))
            }
        }
    }
}