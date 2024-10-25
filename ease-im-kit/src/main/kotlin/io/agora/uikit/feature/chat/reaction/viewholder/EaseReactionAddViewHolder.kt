package io.agora.uikit.feature.chat.reaction.viewholder

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import io.agora.uikit.R
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.databinding.EaseItemMessageMenuReactionBinding
import io.agora.uikit.model.EaseReaction

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