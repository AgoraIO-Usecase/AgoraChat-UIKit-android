package com.hyphenate.easeui.feature.chat.reaction.viewholder

import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.hyphenate.easeui.R
import com.hyphenate.easeui.base.ChatUIKitBaseRecyclerViewAdapter
import com.hyphenate.easeui.databinding.UikitItemMessageMenuReactionBinding
import com.hyphenate.easeui.model.ChatUIKitReaction

class ChatUIKitReactionAddViewHolder(private val binding: UikitItemMessageMenuReactionBinding)
    : ChatUIKitBaseRecyclerViewAdapter.ViewHolder<ChatUIKitReaction>(binding = binding) {

    override fun setData(item: ChatUIKitReaction?, position: Int) {
        item?.let {
            with(binding) {
                ivExpression.background = null
                ivExpression.setImageResource(item.icon)
                ivExpression.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(itemView.context, R.color.ease_color_on_background))
            }
        }
    }
}