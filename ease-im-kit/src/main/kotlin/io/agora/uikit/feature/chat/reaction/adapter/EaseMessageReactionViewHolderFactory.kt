package io.agora.uikit.feature.chat.reaction.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import io.agora.uikit.base.EaseBaseRecyclerViewAdapter
import io.agora.uikit.databinding.EaseItemMessageMenuReactionBinding
import io.agora.uikit.databinding.EaseItemMessageReactionBinding
import io.agora.uikit.feature.chat.enums.EaseReactionType
import io.agora.uikit.feature.chat.reaction.viewholder.EaseReactionAddViewHolder
import io.agora.uikit.feature.chat.reaction.viewholder.EaseReactionDefaultViewHolder
import io.agora.uikit.feature.chat.reaction.viewholder.EaseReactionMoreViewHolder
import io.agora.uikit.feature.chat.reaction.viewholder.EaseReactionNormalViewHolder
import io.agora.uikit.model.EaseReaction

object EaseMessageReactionViewHolderFactory {
    fun createViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EaseBaseRecyclerViewAdapter.ViewHolder<EaseReaction> {
        return when (viewType) {
            EaseReactionType.DEFAULT.viewType -> EaseReactionDefaultViewHolder(
                EaseItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            EaseReactionType.ADD.viewType -> EaseReactionAddViewHolder(
                EaseItemMessageMenuReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )

            EaseReactionType.MORE.viewType -> EaseReactionMoreViewHolder()

            else -> EaseReactionNormalViewHolder(
                EaseItemMessageReactionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false)
            )
        }
    }

    fun getViewType(reaction: EaseReaction?): Int {
        return reaction?.let { getReactionType(it) } ?: (EaseReactionType.NORMAL.viewType)
    }

    private fun getReactionType(reaction: EaseReaction): Int {
        val type = reaction.type
        return type.viewType
    }
}